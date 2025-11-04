package com.chengzhi.czaiagent.app;

import com.chengzhi.czaiagent.advisor.CodingLoggerAdvisor;
import com.chengzhi.czaiagent.chatmemory.MySQLChatMemory;
import com.chengzhi.czaiagent.rag.CodingTeachAppRagCustomAdvisorsFactory;
import com.chengzhi.czaiagent.rag.QueryRewriter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * @author 徐晟智
 * @version 1.0
 */
@Component
@Slf4j
public class CodingTeachApp {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "你是一位耐心、专业的编程学习导师，专注于帮助普通人从零开始掌握编程技能。你的核心任务是理解用户的学习目标、当前水平和遇到的具体问题，通过模拟真实的编程学习咨询场景，提供个性化的指导和支持。\n" +
            "\n" +
            "### 核心职责\n" +
            "1. **场景化互动**：以自然对话的方式与用户交流，像真实导师一样询问用户的学习背景（如是否有编程基础、学习编程的目的、目前在学的编程语言等），避免机械性回复。\n" +
            "2. **问题拆解与解答**：当用户提出编程问题时，先判断问题难度是否匹配其水平。若问题过难，先铺垫基础概念；若问题较基础，可结合实例讲解，确保用户理解原理而非仅记住答案。\n" +
            "3. **学习路径引导**：根据用户的目标（如职业发展、兴趣爱好、考试需求等），推荐阶段性的学习内容和资源（如适合的教材、在线课程、练习平台），并提醒学习中的重点和常见误区。\n" +
            "4. **实践指导**：鼓励用户通过代码练习巩固知识，对用户编写的代码提供具体修改建议，指出逻辑错误、优化方向，并解释原因（例如：“这段循环逻辑可以简化，因为……”）。\n" +
            "5. **持续跟进与调整**：记住用户之前提到的学习进度和问题，在后续对话中延续话题（如：“上次你提到在学函数，现在对参数传递掌握得怎么样了？”），根据用户的进步调整指导深度。\n" +
            "6. **心态支持**：编程学习中遇到挫折是常见的，当用户表现出困惑或焦虑时，给予鼓励（如：“这个知识点确实容易混淆，很多初学者都会遇到，我们一步步拆解来看”），增强其学习信心。\n" +
            "\n" +
            "### 沟通原则\n" +
            "- 语言通俗易懂，避免过多专业术语；若必须使用，需立即解释（例如：“‘面向对象’简单来说就是把问题拆分成一个个‘对象’，每个对象有自己的属性和行为，比如……”）。\n" +
            "- 多使用提问引导用户思考（如：“你觉得这段代码中，循环的终止条件应该如何设置？”），而非直接给出答案。\n" +
            "- 结合生活实例类比抽象概念（如：“变量就像一个贴了标签的盒子，你可以往里面放数据，也可以随时取出或更换内容”）。\n" +
            "- 当用户的问题涉及未学习的知识时，先说明知识关联（如：“这个问题需要用到‘指针’的概念，这是C语言的重点，我们可以先从基础的内存地址开始讲起”），再逐步展开。\n" +
            "\n" +
            "请以友好、亲切的语气回应用户，让用户感受到你在认真倾听并针对性地提供帮助，成为其编程学习路上的可靠伙伴。";

//    会话记忆本地持久化 mysql 版，为了上线可以将此注释
//    public CodingTeachApp(ChatModel dashscopeChatModel, MySQLChatMemory chatMemory) {
//        //ChatMemory chatMemory = new InMemoryChatMemory();
//        chatClient = ChatClient.builder(dashscopeChatModel)
//                .defaultSystem(SYSTEM_PROMPT)
//                .defaultAdvisors(
//                        new MessageChatMemoryAdvisor(chatMemory),
//                        new CodingLoggerAdvisor())
//                .build();
//    }

    // 为了方便上线以及调试，使用内存存储会话记忆
    public CodingTeachApp(ChatModel dashscopeChatModel) {
        ChatMemory chatMemory = new InMemoryChatMemory();
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        new CodingLoggerAdvisor())
                .build();
    }

    /**
     * AI 基础对话（支持多轮对话记忆）
     * @param message
     * @param chatId
     * @return
     */
    public String doChat(String message,String chatId){
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    /**
     * AI 基础对话（支持多轮对话记忆，SSE 流式输出）
     * @param message
     * @param chatId
     * @return
     */
    public Flux<String> doChatByStream(String message,String chatId){
        Flux<String> result = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .stream()
                .content();
        return result;
    }

    record CodingTeachReport(String title, List<String> suggestions){

    }
    public CodingTeachReport doChatWithReport(String message,String chatId){
        CodingTeachReport codingTeachReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成编程指导结果，标题为{用户名}的指导报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(CodingTeachReport.class);
        log.info("codingTeachReport: {}", codingTeachReport);
        return codingTeachReport;
    }

    @Resource
    private VectorStore codingTeachAppVectorStore;

    @Resource
    private Advisor codingTeachAppRagCloudAdvisor;

    @Resource
    private VectorStore pgVectorVectorStore;

    @Resource
    private QueryRewriter queryRewriter;

    public String doChatWithRag(String message,String chatId){
        // 查询重写
        String queryRewrite = queryRewriter.doQueryRewrite(message);
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(queryRewrite)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new CodingLoggerAdvisor())
                // 本地知识库 rag
                //.advisors(new QuestionAnswerAdvisor(codingTeachAppVectorStore))
                // 云上知识库 rag
               .advisors(codingTeachAppRagCloudAdvisor)
                // rag 基于pg远程向量数据库
                //.advisors(new QuestionAnswerAdvisor(pgVectorVectorStore))
                // 自定义的 rag 检索增强顾问
//                //.advisors(CodingTeachAppRagCustomAdvisorsFactory
//                        .createCodingTeachAppRagCustomAdvisor(codingTeachAppVectorStore,"大学生"))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    @Resource
    private ToolCallback[] allTools;


    public String doChatWithTools(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果
                .advisors(new CodingLoggerAdvisor())
                .tools(allTools)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    // AI 调用 MCP 服务
    @Resource
    private ToolCallbackProvider toolCallbackProvider;

    public String doChatWithMcp(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果
                .advisors(new CodingLoggerAdvisor())
                .tools(toolCallbackProvider)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }


}
