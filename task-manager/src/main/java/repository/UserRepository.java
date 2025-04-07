package repository;

import model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Możesz dodać tu dodatkowe zapytania, np. po emailu
    User findByEmail(String email);
}
