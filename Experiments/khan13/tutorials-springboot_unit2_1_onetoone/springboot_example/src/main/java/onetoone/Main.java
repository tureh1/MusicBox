package onetoone;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import onetoone.Users.User;
import onetoone.Users.UserRepository;
import onetoone.Persons.Person;
import onetoone.Persons.PersonRepository;

/**
 * 
 * @author Vivek Bengre
 * 
 */ 

@SpringBootApplication
class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    // Create 3 Persons with their machines
    /**
     * 
     * @param PersonRepository repository for the Person entity
     * @param UserRepository repository for the Laptop entity
     * Creates a commandLine runner to enter dummy data into the database
     * As mentioned in Person.java just associating the Laptop object with the Person will save it into the database because of the CascadeType
     */
    @Bean
    CommandLineRunner initPerson(PersonRepository PersonRepository, UserRepository UserRepository) {
        return args -> {
            Person Person1 = new Person("Haris", "Haris@gmail.com");
            Person Person2 = new Person("Tu", "Tu@aol.com");
            Person Person3 = new Person("Ping", "Ping@hotmail.com");
            Person Person4 = new Person("Bae", "Bae@outlook.com");
            User user1 = new User( "Haris@gmail.com", "Haris123");
            User user2 = new User("Tu@aol.com" , "Tu123");
            User user3 = new User( "Ping@hotmail.com", "Ping123");
            User user4 = new User( "Bae@outlook.com", "Bae123");
            //Person1.setPerson(user1);
            //Person2.setPerson(user2);
            //Person3.setUser(user3);
            PersonRepository.save(Person1);
            PersonRepository.save(Person2);
            PersonRepository.save(Person3);
            PersonRepository

        };
    }

}
