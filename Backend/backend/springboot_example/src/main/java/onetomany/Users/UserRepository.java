package onetomany.Users;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Vivek Bengre
 *
 */

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findById(int id);
    User findByEmailId(String emailId); // Custom method to find user by email
    @Transactional
    void deleteById(int id);
}