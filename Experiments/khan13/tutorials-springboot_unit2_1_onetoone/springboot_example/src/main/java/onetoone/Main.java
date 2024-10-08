package onetoone;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import onetoone.Laptops.Laptop;
import onetoone.Laptops.LaptopRepository;
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
     * @param laptopRepository repository for the Laptop entity
     * Creates a commandLine runner to enter dummy data into the database
     * As mentioned in Person.java just associating the Laptop object with the Person will save it into the database because of the CascadeType
     */
    @Bean
    CommandLineRunner initPerson(PersonRepository PersonRepository, LaptopRepository laptopRepository) {
        return args -> {
            Person Person1 = new Person("Haris", "Haris@gmail.com");
            Person Person2 = new Person("Tu", "Tu@aol.com");
            Person Person3 = new Person("Ping", "Ping@hotmail.com");
            Person Person4 = new Person("Bae", "Bae@outlook.com");
            Laptop laptop1 = new Laptop( "Haris@gmail.com", "Haris123");
            Laptop laptop2 = new Laptop("Tu@aol.com" , "Tu123");
            Laptop laptop3 = new Laptop( "Ping@hotmail.com", "Ping123");
            Laptop laptop4 = new Laptop( "Bae@outlook.com", "Bae123");
            Person1.setLaptop(laptop1);
            Person2.setLaptop(laptop2);
            Person3.setLaptop(laptop3);            
            PersonRepository.save(Person1);
            PersonRepository.save(Person2);
            PersonRepository.save(Person3);

        };
    }

}
