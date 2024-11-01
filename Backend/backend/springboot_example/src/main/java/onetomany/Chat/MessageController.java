package onetomany.Chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/messages")
    public List<Message> getMessagesBetweenUsers(@RequestParam String email, @RequestParam String friendEmail) {
        // Retrieve messages where email is either the sender or the recipient
        List<Message> messagesFromSender = messageRepository.findByUserNameAndFriendEmail(email, friendEmail);
        List<Message> messagesFromRecipient = messageRepository.findByUserNameAndFriendEmail(friendEmail, email);

        messagesFromSender.addAll(messagesFromRecipient); // Combine both directions
        return messagesFromSender;
    }
}
