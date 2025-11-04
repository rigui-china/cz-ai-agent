package com.chengzhi.czaiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 徐晟智
 * @version 1.0
 */
@SpringBootTest
class FileOperationToolTest {

    @Test
    void readFile() {
        FileOperationTool tool = new FileOperationTool();
        String fileName = "编程学习.txt";
        String result = tool.readFile(fileName);
        Assertions.assertNotNull(result);
    }

    @Test
    void writeFile() {
        FileOperationTool tool = new FileOperationTool();
        String fileName = "编程学习.txt";
        String content = "学编程就来编程导航";
        String result = tool.writeFile(fileName, content);
        Assertions.assertNotNull(result);
    }
}