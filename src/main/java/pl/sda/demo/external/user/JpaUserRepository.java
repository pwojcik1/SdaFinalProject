package pl.sda.demo.external.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, Integer> {
    @Query("select u from UserEntity u where u.username =:username")
    Optional<UserEntity> getUserByName(@Param("username") String username);
}
