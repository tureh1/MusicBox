package onetomany.Profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Integer> {

    // Custom method to find a profile by userId
    Optional<Profile> findByUserId(int userId);
}