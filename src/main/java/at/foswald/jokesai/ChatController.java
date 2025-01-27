package at.foswald.jokesai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// class level Javadoc
@RestController
@RequestMapping("/ai")
class MyController {

    private final ChatClient chatClient;
    ListOutputConverter listOutputConverter = new ListOutputConverter(new DefaultConversionService());
    MapOutputConverter mapOutputConverter = new MapOutputConverter();
    BeanOutputConverter beanOutputConverter = new BeanOutputConverter(Author.class);

    public MyController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .build();
    }

    //region simple prompts
    // doc
    @GetMapping("/joke")
    public String generalJoke() {
        return this.chatClient.prompt()
                .user("Erzähl mir einen Witz")
                .call()
                .content();
    }

    @GetMapping("/themeJoke")
    public String theme(@RequestParam(value = "theme", defaultValue = "programmieren") String theme) {

        String message = String.format("Erzähl mir einen Witz über %s.\n" +
                "Wenn du die Antwort nicht kennst, sag einfach \"Ich weiß nicht.\"", theme);

        return this.chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    @GetMapping("/childrenJoke")
    public String children(@RequestParam(value = "theme", defaultValue = "Bauern") String theme) {

        String messageSystem = "Deine primäre Funktion sollte sein Kinderfreundliche Witze zu erzählen\n" +
                "Wenn dich jemand nach anderen Witzen fragt, sag \"Ich kenne nur kinderfreundliche Witzen\"";

        String messageUser = String.format("Erzähl mir einen Witz über %s.\n" +
                "Wenn du die Antwort nicht kennst, sag einfach \"Ich weiß nicht.\"", theme);

        return this.chatClient.prompt()
                .system(messageSystem)
                .user(messageUser)
                .call()
                .content();
    }
    //endregion

    //region outputParsers
    @GetMapping("/songs/top10byArtist")
    public List<String> songs(@RequestParam(value = "artist", defaultValue = "Taylor Swift") String artist) {

        String messageSystem = String.format("""
                Deine primäre Funktion sollte sein Top 10 songs von diversen Künstlern zurück zu geben
                Wenn du den Künstler nicht kennst, sag "Ich kenne diesen Künstler nicht."
                Wenn der Künstler keine Top 10 Songs hat, sag "Dieser Künstler hat keine Top 10 Songs."
                Hier ist das Format in dem ich meine Antwort erwarte %s""", listOutputConverter.getFormat());

        String messageUser = String.format("Bitte gib mir eine Liste von Top 10 Songs des Künstlers %s.", artist);

        return listOutputConverter.convert(this.chatClient.prompt()
                .system(messageSystem)
                .user(messageUser)
                .call()
                .content());
    }

    @GetMapping("/social-links/fromAuthor/{author}")
    public Map<String, Object> getAuthorsSocialLinks(@PathVariable String author) {

        String messageSystem = String.format("""
                Deine primäre Funktion sollte sein die Social Media Link diverser Buch Autoren zurück zu geben
                Wenn du den Autor nicht kennst, sag "Ich kenne diesen Autor nicht."
                Wenn der Autor keine Social Media Links hat, sag "Dieser Autor hat keine Social Media Links."
                Hier ist das Format in dem ich meine Antwort erwarte %s""", mapOutputConverter.getFormat());

        String messageUser = String.format("Bitte gib mir die Social Media Links des Authors %s.", author);

        return mapOutputConverter.convert(this.chatClient.prompt()
                .system(messageSystem)
                .user(messageUser)
                .call()
                .content());
    }

    // Javadoc
    @GetMapping("/books/byAuthor/{author}")
    public Object getBooksByAuthor(@PathVariable String author) {
        String systemPrompt =
                "Your response should be in JSON format. " +
                        "Do not include any explanations, only provide a RFC8259 compliant JSON response following this format without deviation. " +
                        "Do not include markdown code blocks in your response. Remove the ```json markdown from the output. " +
                        "Here is the JSON Schema instance your output must adhere to: { \"$schema\": \"https://json-schema.org/draft/2020-12/schema\", \"type\": \"object\", \"properties\": { \"books\": { \"type\": \"array\", \"items\": { \"type\": \"string\" } }, \"name\": { \"type\": \"string\" } } }. ";

        String userPrompt = String.format("Bitte gib mir die Bücher des Authors %s.", author);

        String requestPrompt = String.format("{\"prompt\":\"%s %s\",\"max_tokens\":1000}", systemPrompt, userPrompt);

        return beanOutputConverter.convert(this.chatClient.prompt()
//            .system(prompt)
                .user(requestPrompt)
                .call()
                .content());
    }
    //endregion
}