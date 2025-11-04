package com.chengzhi.czaiagent.agent;

/**
 * @author 徐晟智
 * @version 1.0
 */

import cn.hutool.core.util.StrUtil;
import com.chengzhi.czaiagent.agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 抽象基础代理类，用于管理执行状态和执行流程
 * 子类必须实现 step 方法
 */
@Data
@Slf4j
public abstract class BaseAgent {

    // 核心属性
    private String name;

    // 提示词
    private String systemPrompt;
    private String nextStepPrompt;

    // 代理状态
    private AgentState status = AgentState.IDLE;

    // 执行步骤次数
    private int currentStep = 0;
    private int maxSteps = 10;

    // 传入的大模型客户端
    private ChatClient chatClient;

    // 对话上下文
    List<Message> messageList = new ArrayList<>();


    // 具体执行方法
    public String run(String userPrompt){
        // 1、校验参数和状态
        if(this.status != AgentState.IDLE){
            throw new RuntimeException("Cannot run from this state: " + this.status);
        }
        if(StrUtil.isBlank(userPrompt)){
            throw new RuntimeException("userPrompt is empty");
        }
        // 2、状态变换
        this.status = AgentState.RUNNING;
        // 3、具体执行流程
        // 将用户提示词添加对话记忆列表的
        messageList.add(new UserMessage(userPrompt));
        // 保存结果
        List<String> results = new ArrayList<>();
        try {
            for(int i = 0; i < maxSteps && status != AgentState.FINISHED; i++){
                int stepNumber = i + 1;
                currentStep = stepNumber;
                log.info("Executing step {}/{}",stepNumber,maxSteps);
                String stepResult = step();
                String result = "Step: " + stepNumber + " Result: " + stepResult;
                results.add(result);
            }
            // 检查是否超出步骤限制
            if(currentStep >= maxSteps){
                status = AgentState.FINISHED;
                results.add("Terminated: Reached maxSteps (" + maxSteps + ")");
            }
            return String.join("\n", results);
        } catch (Exception e) {
            status = AgentState.ERROR;
            log.error("error executing step", e);
            return "执行错误" + e.getMessage();
        }finally {
            cleanup();
        }
    }


    /**
     * SSE 流式输出
     * @param userPrompt
     * @return
     */
    public SseEmitter runStream(String userPrompt){
        // 定义超时时间
        SseEmitter sseEmitter = new SseEmitter(300000L);

        CompletableFuture.runAsync(()->{
            try {
                // 1、校验参数和状态
                if(this.status != AgentState.IDLE){
                    sseEmitter.send("错误：无法从此状态: " + this.status + " 运行代理");
                    sseEmitter.complete();
                    return;
                }
                if(StrUtil.isBlank(userPrompt)){
                    sseEmitter.send("错误：不能使用空提示词运行代理 !");
                    sseEmitter.complete();
                    return;
                }
            } catch (IOException e) {
                sseEmitter.completeWithError(e);
            }
            // 2、状态变换
            this.status = AgentState.RUNNING;
            // 3、具体执行流程
            // 将用户提示词添加对话记忆列表的
            messageList.add(new UserMessage(userPrompt));
            // 保存结果
            List<String> results = new ArrayList<>();
            try {
                for(int i = 0; i < maxSteps && status != AgentState.FINISHED; i++){
                    int stepNumber = i + 1;
                    currentStep = stepNumber;
                    log.info("Executing step {}/{}",stepNumber,maxSteps);
                    String stepResult = step();
                    String result = "Step: " + stepNumber + " Result: " + stepResult;
                    results.add(result);
                    // 输出当前的每一步到 SSE
                    sseEmitter.send(result);
                }
                // 检查是否超出步骤限制
                if(currentStep >= maxSteps){
                    status = AgentState.FINISHED;
                    results.add("Terminated: Reached maxSteps (" + maxSteps + ")");
                    sseEmitter.send("执行结束：达到最大步骤数 (" + maxSteps + ")");
                }
                sseEmitter.complete();
            } catch (Exception e) {
                status = AgentState.ERROR;
                log.error("error executing step", e);
                try {
                    sseEmitter.send("执行错误：" + e.getMessage());
                    sseEmitter.complete();
                } catch (IOException ex) {
                    sseEmitter.completeWithError(ex);
                }
            }finally {
                cleanup();
            }
        });

        // 设置超时回调
        sseEmitter.onTimeout(()->{
            this.status = AgentState.ERROR;
            this.cleanup();
            log.warn("SSE connection timed out");
        });

        sseEmitter.onCompletion(() -> {
            if(this.status == AgentState.RUNNING){
                this.status = AgentState.FINISHED;
            }
            this.cleanup();
            log.info("SSE connection completed");
        });

        return sseEmitter;
    }


    /**
     * 具体每一步执行，子类重写此方法
     * @return
     */
    public abstract String step();

    /**
     * 清理资源
     */
    protected void cleanup(){

    }


}
