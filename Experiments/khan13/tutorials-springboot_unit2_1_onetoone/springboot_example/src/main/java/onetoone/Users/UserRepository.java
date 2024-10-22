package onetoone.Users;

import onetoone.Laptops.Laptop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Vivek Bengre
 *
 */

public interface UserRepository extends JpaRepository<User, Long> {
    Laptop findById(int id);

    @Transactional
    void deleteById(int id);
}
