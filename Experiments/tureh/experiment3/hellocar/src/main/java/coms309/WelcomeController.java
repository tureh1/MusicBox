package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * Simple Hello World Controller to display the string returned
 *
 * @author Vivek Bengre
 */

@RestController
class WelcomeController {

    private String[] quotes = {
            "Cars go vroom.",
            "How fast can you go?",
            "VROOOOOOMMMMMMMMm!!",
            "Life is a journey, enjoy the ride.",
            "Keep calm and drive on.",
            "Drive safe, arrive safe.",
            "Speed thrills but kills."
    };

    @GetMapping("/")
    public String welcome() {
        String message = "      ______\n" +
                "  __//__|__\\\\__\n" +
                " /  _     _    \\\n" +
                "'--(_)---(_)--'\n" +
                "Hello Cars, use directory '/help' for Command List\n";
        return formatMessage(message);
    }

    @GetMapping("/help")
    public String commands() {
        return "CommandsList:\n " + "'/people' to create a new user or view all listed users \n " +
                "'/people/{firstName}' to view users containing fist name or to edit user info or delete \n ";
    }

    private String formatMessage(String message) {
        String timestamp = LocalDateTime.now().toString(); // Get time
        String quote = quotes[new Random().nextInt(quotes.length)]; // Select random quote
        return message + "\nCurrent time: " + timestamp + "\n " + quote;
    }
}
