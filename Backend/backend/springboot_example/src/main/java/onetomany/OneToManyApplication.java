package onetomany;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import onetomany.Users.User;
import onetomany.Users.UserRepository;

@SpringBootApplication
public class OneToManyApplication {

    public static void main(String[] args) {
        SpringApplication.run(OneToManyApplication.class, args);
    }

    // Create some users for initial testing
    @Bean
    public CommandLineRunner demo(UserRepository userRepository) {
        return args -> {
            // Add some test users
            userRepository.save(new User("user1", "password1"));
            userRepository.save(new User("user2", "password2"));
            userRepository.save(new User("user3", "password3"));

            System.out.println("Initial users created:");
            userRepository.findAll().forEach(user -> System.out.println("User ID: " + user.getId()));
        };
    }
}
