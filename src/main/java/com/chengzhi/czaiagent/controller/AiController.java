package com.chengzhi.czaiagent.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.chengzhi.czaiagent.agent.CZManus;
import com.chengzhi.czaiagent.app.CodingTeachApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

/**
 * @author 徐晟智
 * @version 1.0
 */
@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private CodingTeachApp codingTeachApp;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel dashscopeChatModel;

    /**
     * 同步调用 AI 编程学习导师应用
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping("/codingTeach_app/chat/sync")
    public String doChatWithCodingTeachAppSync(String message,String chatId) {
        return codingTeachApp.doChat(message,chatId);
    }


    /**
     * SSE 调用 AI 编程学习导师应用
     * produces 作用是 http 头里面填充流式返回字段
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/codingTeach_app/chat/sse",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithCodingTeachAppSSE(String message, String chatId) {
        return codingTeachApp.doChatByStream(message, chatId);
    }

    /**
     * SSE 调用 AI 编程学习导师应用
     * 框架会自动添加 http 头
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/codingTeach_app/chat/server_sent_event")
    public Flux<ServerSentEvent<String>> doChatWithCodingTeachAppServerSentEvent(String message, String chatId) {
        return codingTeachApp.doChatByStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }

    /**
     * SSE 调用 AI 编程学习导师应用
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/codingTeach_app/chat/sse_emitter")
    public SseEmitter doChatWithCodingTeachAppSseEmitter(String message, String chatId) {
        // 定义超时时间
        SseEmitter sseEmitter = new SseEmitter(180000L);
        // 获取 flux 响应流并且直接通过订阅推送给 SseEmitter
        codingTeachApp.doChatByStream(message, chatId)
                .subscribe(chunk -> {
                    try {
                        sseEmitter.send(chunk);
                    } catch (IOException e) {
                        sseEmitter.completeWithError(e);
                    }
                },sseEmitter::completeWithError,sseEmitter::complete);
        return sseEmitter;
    }

    /**
     * 流式调用 manus 智能体
     * @param message
     * @return
     */
    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(String message){
        CZManus czManus = new CZManus(allTools, dashscopeChatModel);
        return czManus.runStream(message);
    }
}
