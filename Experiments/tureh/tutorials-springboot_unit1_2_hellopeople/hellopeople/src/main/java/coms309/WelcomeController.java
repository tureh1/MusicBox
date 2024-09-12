package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple Hello World Controller to display the string returned
 *
 * @author Vivek Bengre
 */

@RestController
class WelcomeController {

    @GetMapping("/")
    public String welcome() {
        return "Hello and welcomes to COMS 309";
    }

    @GetMapping("/help")
    public String commands() {
        return "CommandsList:\n " + "'/people' to create a new user or view all listed users \n " +
                "'/people/{firstName}' to view users containing fist name or to edit user info or delete \n ";

    }
}
