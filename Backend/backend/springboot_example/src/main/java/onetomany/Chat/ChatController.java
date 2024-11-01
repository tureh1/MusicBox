package onetomany.Chat;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/sendMessage") // Maps to /app/sendMessage
    @SendTo("/topic/messages") // Broadcasts to all subscribers of /topic/messages
    public ChatMessage sendMessage(ChatMessage message) {
        // You might want to log the message or implement additional logic here
        return message; // Return the message to be sent to subscribers
    }
}

