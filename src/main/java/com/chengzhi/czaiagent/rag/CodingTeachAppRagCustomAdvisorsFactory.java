package com.chengzhi.czaiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

/**
 * @author 徐晟智
 * @version 1.0
 */

/**
 * 自定义的 RAG 检索增强顾问的工厂
 */
@Slf4j
public class CodingTeachAppRagCustomAdvisorsFactory {
    public static Advisor createCodingTeachAppRagCustomAdvisor(VectorStore vectorStore,String status){

        Filter.Expression expression = new FilterExpressionBuilder()
                .eq("status", status)
                .build();


        VectorStoreDocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .similarityThreshold(0.5)
                .topK(3)
                .filterExpression(expression)
                .build();

        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .queryAugmenter(CodingTeachAppContextualQueryAugmenterFactory.createContextualQueryAugmenter())
                .build();
    }
}
