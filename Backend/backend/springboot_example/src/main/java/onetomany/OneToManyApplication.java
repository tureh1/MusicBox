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
/*
package onetomany;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import onetomany.Album.Album;
import onetomany.Album.AlbumRepository;

@SpringBootApplication
public class OneToManyApplication {

    public static void main(String[] args) {
        SpringApplication.run(OneToManyApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(AlbumRepository albumRepository) {
        return (args) -> {
            albumRepository.save(new Album("In the Lonely Hour", "Sam Smith", "Pop", 2014, "https://example.com/lonelyhour.jpg"));
            albumRepository.save(new Album("25", "Adele", "Pop", 2015, "https://example.com/adele25.jpg"));
            albumRepository.save(new Album("Random Access Memories", "Daft Punk", "Electronic", 2013, "https://example.com/randomaccess.jpg"));
            albumRepository.save(new Album("To Pimp a Butterfly", "Kendrick Lamar", "Hip-Hop", 2015, "https://example.com/topimpabutterfly.jpg"));
            albumRepository.save(new Album("DAMN.", "Kendrick Lamar", "Hip-Hop", 2017, "https://example.com/damn.jpg"));
        };
    }
}
*/
