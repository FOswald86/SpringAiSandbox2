package at.foswald.jokesai;

import at.foswald.jokesai.Author;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class OpenAIService {

    @Value("${spring.ai.openai.api-key}")
    private String openaiApiKey;

    private final ObjectMapper objectMapper;

    public OpenAIService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

//    public Author getBooksByAuthor(String author) {
//        String prompt = String.format(
//                "Deine primäre Funktion sollte sein die Bücher der Autoren zurück zu geben. " +
//                        "Wenn du den Autor nicht kennst, sag \"Ich kenne diesen Autor nicht.\" " +
//                        "Wenn der Autor keine Bücher hat, sag \"Dieser Autor hat keine Bücher.\" " +
//                        "Hier ist das Format in dem ich meine Antwort erwarte: Your response should be in JSON format. " +
//                        "Do not include any explanations, only provide a RFC8259 compliant JSON response following this format without deviation. " +
//                        "Do not include markdown code blocks in your response. Remove the ```json markdown from the output. " +
//                        "Here is the JSON Schema instance your output must adhere to: { \"$schema\": \"https://json-schema.org/draft/2020-12/schema\", \"type\": \"object\", \"properties\": { \"books\": { \"type\": \"array\", \"items\": { \"type\": \"string\" } }, \"name\": { \"type\": \"string\" } } }. " +
//                        "Bitte gib mir die Bücher des Authors %s.", author);
//
//        String requestJson = String.format("{\"prompt\":\"%s\",\"max_tokens\":1000}", prompt);
//
//        Mono<String> responseMono = webClient.post()
//                .header("Authorization", "Bearer " + openaiApiKey)
//                .header("Content-Type", "application/json")
//                .bodyValue(requestJson)
//                .retrieve()
//                .bodyToMono(String.class);
//
//        String jsonResponse = responseMono.block();  // Blocking call should be avoided in production
//
//        return convertToBeanOutput(jsonResponse);
//    }

    private Author convertToBeanOutput(String jsonResponse) {
        try {
            return objectMapper.readValue(jsonResponse, Author.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
