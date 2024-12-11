package onetomany.Chat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;

@RestController
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/messages")
    @Operation(
            summary = "Get messages between users",
            description = "Fetches the the messages between users"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved messages",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "invalid request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public List<FormattedMessage> getMessagesBetweenUsers(@RequestParam String email, @RequestParam String friendEmail) {
        List<Message> messagesFromSender = messageRepository.findByUserNameAndFriendEmail(email, friendEmail);
        List<Message> messagesFromRecipient = messageRepository.findByUserNameAndFriendEmail(friendEmail, email);

        List<FormattedMessage> formattedMessages = new ArrayList<>();

        for (Message message : messagesFromSender) {
            formattedMessages.add(new FormattedMessage(
                    message.getUserName(),
                    message.getContent(),
                    "user-message",
                    message.getFormattedTimestamp()  // Include timestamp
            ));
        }

        for (Message message : messagesFromRecipient) {
            formattedMessages.add(new FormattedMessage(
                    message.getUserName(),
                    message.getContent(),
                    "friend-message",
                    message.getFormattedTimestamp()  // Include timestamp
            ));
        }

        return formattedMessages;
    }

    // Modify FormattedMessage class to include timestamp
    public static class FormattedMessage {
        private String sender;
        private String content;
        private String type;
        private String timestamp;

        public FormattedMessage(String sender, String content, String type, String timestamp) {
            this.sender = sender;
            this.content = content;
            this.type = type;
            this.timestamp = timestamp;
        }


        public String getSender() {
            return sender;
        }

        public String getContent() {
            return content;
        }

        public String getType() {
            return type;
        }
    }
}
