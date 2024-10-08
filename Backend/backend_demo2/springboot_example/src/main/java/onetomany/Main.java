package onetomany;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import onetomany.Users.User;
import onetomany.Users.UserRepository;

@SpringBootApplication
class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    // Create some users for initial testing
    @Bean
    CommandLineRunner initPerson(UserRepository userRepository) {
        return args -> {
            User user1 = new User("Haris@gmail.com", "Haris123");
            User user2 = new User("Tu@aol.com", "Tu123");
            User user3 = new User("Ping@hotmail.com", "Ping123");
            User user4 = new User("Bae@outlook.com", "Bae123");
            //setUser(user1);
            //setUser(user2);
            //setUser(user3);
            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);
        };
    }
}
