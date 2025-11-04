package com.chengzhi.czaiagent.agent;

/**
 * @author 徐晟智
 * @version 1.0
 */

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.chengzhi.czaiagent.agent.model.AgentState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理工具调用的基础代理类，具体实现了 think 和 act 方法，可以用作创建实例的父类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ToolCallAgent extends ReActAgent{
    // 可用的工具列表
    private ToolCallback[] availableTools;

    // 工具调用的结果
    private ChatResponse toolCallChatResponse;

    // 工具调用管理器
    private ToolCallingManager toolCallingManager;

    // 关闭 Spring AI 自主执行工具调用的选项，让自己来维护消息上下文和执行工具调用流程
    private final ChatOptions chatOptions;

    public ToolCallAgent(ToolCallback[] availableTools){
        super();
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        // 自主执行工具调用和维护消息上下文的选项
        this.chatOptions = DashScopeChatOptions.builder().withProxyToolCalls(true).build();
    }

    /**
     * 处理当前状态并决定下一步行动
     * @return
     */
    @Override
    public boolean think() {
        // 1、校验提示词，拼接用户提示词
        if(StrUtil.isNotBlank(getNextStepPrompt())){
            getMessageList().add(new UserMessage(getNextStepPrompt()));
        }
        // 2、调用 AI 大模型，获取工具调用列表
        List<Message> messageLists = getMessageList();
        Prompt prompt = new Prompt(messageLists,this.chatOptions);
        try {
            ChatResponse chatResponse = getChatClient().prompt(prompt)
                    .system(getSystemPrompt())
                    .tools(availableTools)
                    .call()
                    .chatResponse();
            // 记录响应，用于 act
            this.toolCallChatResponse = chatResponse;
            // 助手消息
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            // 可用的工具列表
            List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();
            // 思考的结果
            String result = assistantMessage.getText();
            log.info(getName() + "的思考: " + result);
            log.info(getName() + "选择了 " + toolCallList.size() + " 个工具来使用");
            String toolCallInfo = toolCallList.stream()
                    .map(toolCall -> String.format("工具名称: %s , 参数: %s", toolCall.name(), toolCall.arguments()))
                    .collect(Collectors.joining("\n"));
            log.info(toolCallInfo);
            // 3、解析工具调用列表，获取要调用的工具
            if(toolCallList.isEmpty()){
                // 如果可调用工具列表为空的时候，才需要记录助手消息
                getMessageList().add(assistantMessage);
                return false;
            }else{
                // 如果工具调用列表不为空的话，无需自己记录助手消息，会自动记录
                return true;
            }
        } catch (Exception e) {
            log.error(getName() + "的思考过程中遇到了困难: " + e.getMessage());
            getMessageList().add(new AssistantMessage("处理时遇到错误: " + e.getMessage()));
            return false;
        }
    }

    /**
     * 执行工具调用并返回结果
     * @return
     */
    @Override
    public String act() {
        if(!toolCallChatResponse.hasToolCalls()){
            return "没有工具需要调用";
        }
        Prompt prompt = new Prompt(getMessageList(), this.chatOptions);
        // 工具调用
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);
        // 保存消息上下文
        // 这里的 conversationHistory 包含之前的消息上下文以及工具的执行结果
        setMessageList(toolExecutionResult.conversationHistory());
        // 获取工具调用的结果
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());
        String results = toolResponseMessage.getResponses().stream()
                .map(toolResponse -> "工具 " + toolResponse.name() + " 返回的结果是: " + toolResponse.responseData())
                .collect(Collectors.joining("\n"));

        // 判断是否调用了终止工具
        boolean ifTernimated = toolResponseMessage.getResponses().stream()
                .anyMatch(response -> response.name().equals("doTerminate"));
        if(ifTernimated){
            setStatus(AgentState.FINISHED);
        }
        log.info(results);
        return results;
    }

}
