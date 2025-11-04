package com.chengzhi.czaiagent.dao;

/**
 * @author 徐晟智
 * @version 1.0
 */


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chengzhi.czaiagent.mapper.ConversationMemoryMapper;
import com.chengzhi.czaiagent.model.domain.ConversationMemory;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class ConversationMemoryDAO extends ServiceImpl<ConversationMemoryMapper, ConversationMemory> {


    public List<ConversationMemory> getMessages(String conversationId) {
        return this.lambdaQuery()
                .eq(ConversationMemory::getConversationId, conversationId)
                .list();
    }

    public boolean deleteMemory(String conversationId) {
        return this.lambdaUpdate()
                .eq(ConversationMemory::getConversationId, conversationId)
                .remove();
    }
}

