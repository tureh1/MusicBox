package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import java.time.LocalDateTime;
import java.util.Random;

@RestController
class WelcomeController {

    //An Array of quotes to be randomly selected
    private String[] quotes = {
            "Believe in yourself.",
            "Persevere and don't give up.",
            "You are stronger than you think."
    };

    @GetMapping("/")
    public String welcome() {
        return formatMessage("Hello and welcome to COMS 309");
    }

    //@PathVariable String name method runs a format with name included
    @GetMapping("/{name}")
    public String welcome(@PathVariable String name) {
        return formatMessage("Hello " + name + " and welcome to COMS 309");
    }

    private String formatMessage(String message) {
        String timestamp = LocalDateTime.now().toString(); //Get time
        String quote = quotes[new Random().nextInt(quotes.length)]; //Select random quotes
        return message + "\nCurrent time: " + timestamp + "\nQuote: " + quote;
    }
}
