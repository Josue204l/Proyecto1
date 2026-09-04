package services;

import data.Data;
import logic.Categoria;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

import java.time.LocalDate;
import java.util.stream.Collectors;

public class AIService {

    public ReservaExtraccion extraerReserva(String frase) {
        OpenAiChatModel aiModel = OpenAiChatModel.builder()
                .baseUrl("http://langchain4j.dev/demo/openai/v1")
                .apiKey("demo")
                .modelName("gpt-4o-mini")
                .build();

        ReservaExtractorService aiService = AiServices.create(ReservaExtractorService.class, aiModel);

        String listaCategorias = "";
        if (Data.getInstancia() != null && Data.getInstancia().getCategorias() != null) {
            listaCategorias = Data.getInstancia().getCategorias().stream()
                    .map(Categoria::getNombre)
                    .collect(Collectors.joining(", "));
        }

        return aiService.extraer(frase, listaCategorias, LocalDate.now().toString());
    }
}