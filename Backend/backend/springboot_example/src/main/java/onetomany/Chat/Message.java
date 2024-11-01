package onetomany.Chat;

import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.persistence.*;

import lombok.Data;

@Entity
@Table(name = "messages")
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String userName; // Sender's email

    @Column
    private String friendEmail; // Recipient's email

    @Lob
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sent")
    private Date sent = new Date();

    public Message() {}

    public Message(String userName, String friendEmail, String content) {
        this.userName = userName;
        this.friendEmail = friendEmail;
        this.content = content;
        this.sent = new Date();  // Sets timestamp when the message is created
    }

    // Getter and Setter for friendEmail

    public String getFormattedTimestamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
        return formatter.format(this.sent);
    }
    public String getFriendEmail() {
        return friendEmail;
    }


}
