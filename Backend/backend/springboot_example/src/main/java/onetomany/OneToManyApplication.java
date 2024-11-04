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
}

