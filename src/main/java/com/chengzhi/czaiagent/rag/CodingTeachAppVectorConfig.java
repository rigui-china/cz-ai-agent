package com.chengzhi.czaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author 徐晟智
 * @version 1.0
 */
@Configuration
public class CodingTeachAppVectorConfig {

    @Resource
    private  CodingTeachAppDocumentReader codingTeachAppDocumentReader;

    @Resource
    private MyKeywordEnricher myKeywordEnricher;

    @Bean
    VectorStore codingTeachAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel)
                .build();
        // 加载文档
        List<Document> documents = codingTeachAppDocumentReader.loadMarkdowns();
        // 这里使用关键词元信息增强器
        List<Document> enrichDocuments = myKeywordEnricher.enrichDocuments(documents);
        simpleVectorStore.add(enrichDocuments);
        return simpleVectorStore;
    }

}
