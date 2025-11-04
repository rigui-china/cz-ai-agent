package com.chengzhi.czaiagent.app;

import cn.hutool.core.lang.UUID;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 徐晟智
 * @version 1.0
 */
@SpringBootTest
class CodingTeachAppTest {

    @Resource
    private CodingTeachApp codingTeachApp;

    @Test
    void doChat() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是橙汁";
        String result = codingTeachApp.doChat(message,chatId);
        // 第二轮
        message = "我想学习java";
        result = codingTeachApp.doChat(message,chatId);
        Assertions.assertNotNull(result);
        // 第三轮
        message = "我想学习什么编程语言？我刚才给你说过，帮我回忆一下";
        result = codingTeachApp.doChat(message,chatId);
        Assertions.assertNotNull(result);

    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好，我是橙汁，我想学习 java，但是我不知道该怎么学习";
        CodingTeachApp.CodingTeachReport codingTeachReport = codingTeachApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(codingTeachReport);
    }

    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好，我是橙汁，我现在是一名大学生，想学习编程，但是我不知道该怎么学习";
        String result = codingTeachApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(result);
    }

    @Test
    void doChatWithTools() {
        // 测试联网搜索问题的答案
        testMessage("我是一个编程初学者，请帮我指定一份编程学习计划");

        // 测试网页抓取：恋爱案例分析
        testMessage("最近学编程遇到困难了，看看编程导航网站（codefather.cn）的其他用户是怎么解决问题的？");

        // 测试资源下载：图片下载
        testMessage("直接下载一张适合做手机壁纸的编程相关的图片为文件");

        // 测试终端操作：执行代码
        testMessage("执行 Python3 脚本来生成数据分析报告");

        // 测试文件操作：保存用户档案
        testMessage("保存我的编程指导为文件");

        // 测试 PDF 生成
        testMessage("生成一份‘编程学习计划’PDF，包含学习语言、学习计划和学习目标");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = codingTeachApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithMcp() {
        String chatId = UUID.randomUUID().toString();
        // 测试图片搜索 MCP
        String message = "帮我搜索一些哄另一半开心的图片";
        String answer =  codingTeachApp.doChatWithMcp(message, chatId);
        Assertions.assertNotNull(answer);
    }

}