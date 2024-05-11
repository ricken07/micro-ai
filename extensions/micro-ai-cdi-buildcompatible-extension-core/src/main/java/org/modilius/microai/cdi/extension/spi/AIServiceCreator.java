package org.modilius.microai.cdi.extension.spi;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.build.compatible.spi.Parameters;
import jakarta.enterprise.inject.build.compatible.spi.SyntheticBeanCreator;
import jakarta.enterprise.inject.spi.CDI;
import org.modilius.microai.cdi.extension.MicroAICDIBuildCompatibleExtension;

public class AIServiceCreator implements SyntheticBeanCreator<Object> {
    @Override
    public Object create(Instance<Object> lookup, Parameters params) {
        Class<?> interfaceClass = params.get(MicroAICDIBuildCompatibleExtension.PARAM_INTERFACE_CLASS, Class.class);
        RegisterAIService annotation = interfaceClass.getAnnotation(RegisterAIService.class);

        Class<? extends ChatLanguageModel> chatLanguageModelClass = annotation.model();
        ChatLanguageModel chatLanguageModel = CDI.current().select(chatLanguageModelClass).get();

        try {
            AiServices<?> aiServices = AiServices.builder(interfaceClass)
                    .chatLanguageModel(chatLanguageModel)
                    .tools((Object[]) annotation.tools())
                    .chatMemory(MessageWindowChatMemory.withMaxMessages(annotation.chatMemoryMaxMessages()));

            Instance<ContentRetriever> contentRetrievers = CDI.current().select(ContentRetriever.class);
            if (contentRetrievers.isResolvable())
                aiServices.contentRetriever(contentRetrievers.get());

            return aiServices.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
