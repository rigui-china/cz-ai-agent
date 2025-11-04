package com.chengzhi.czaiagent.chatmemory;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.chengzhi.czaiagent.dao.ConversationMemoryDAO;
import com.chengzhi.czaiagent.model.domain.ConversationMemory;
import com.chengzhi.czaiagent.model.enmu.MessageTypeEnum;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 徐晟智
 * @version 1.0
 */
@Component
public class MySQLChatMemory implements ChatMemory {

    private final ConversationMemoryDAO conversationMemoryDAO;

    public MySQLChatMemory(ConversationMemoryDAO conversationMemoryDAO) {
        this.conversationMemoryDAO = conversationMemoryDAO;
    }


    @Override
    public void add(String conversationId, List<Message> messages) {
        Gson gson = new Gson();
        List<ConversationMemory> memoryList = messages.stream().map(
                message -> {
                    String messageType = message.getMessageType().getValue();
                    String mes = gson.toJson(message);
                    return ConversationMemory.builder().conversationId(conversationId)
                            .type(messageType).memory(mes).build();
                }
        ).toList();
        conversationMemoryDAO.saveBatch(memoryList);
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        List<ConversationMemory> messages = conversationMemoryDAO.getMessages(conversationId);
        if(CollectionUtil.isEmpty(messages)){
            return List.of();
        }
        return messages.stream()
                .skip(Math.max(0, messages.size() - lastN))
                .map(this::getMessage)
                .collect(Collectors.toList());
    }

    @Override
    public void clear(String conversationId) {
        conversationMemoryDAO.deleteMemory(conversationId);
    }
    private Message getMessage(ConversationMemory conversationMemory) {
        String memory = conversationMemory.getMemory();
        Gson gson = new Gson();
        return (Message) gson.fromJson(memory, MessageTypeEnum.fromValue(conversationMemory.getType()).getClazz());
    }
}
