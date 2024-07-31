package at.foswald.jokesai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/joke")
class MyController {

    private final ChatClient chatClient;

    public MyController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .build();
    }

    @GetMapping("/general")
    public String generalJoke() {
        return this.chatClient.prompt()
                .user("Erzähl mir einen Witz")
                .call()
                .content();
    }

    @GetMapping("/theme")
    public String theme(@RequestParam(value = "theme", defaultValue = "programmieren") String theme) {

        String message = String.format("Erzähl mir einen Witz über %s.\n" +
                "Wenn du die Antwort nicht kennst, sag einfach \"Ich weiß nicht.\"", theme);

        return this.chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    @GetMapping("/children")
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
}
