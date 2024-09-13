package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;

@RestController
class WelcomeController {


    @GetMapping("/")
    public String welcome() {
        return "Hello and welcome to COMS 309";
    }

    @GetMapping("/{name}")
    public String welcome(@PathVariable String name) {
        return "Hello and welcome to COMS 309: " + name;
    }

    private String loginMessage(String message)
    {
        String time = LocalDateTime.now().toString();
        String prompt = "Login successful";
        return message + "\nLast login attempt: " + time + "\n: " + prompt;

    }
}
