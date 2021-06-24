package pl.sda.demo.external.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<UserEntity, Integer> {

    @Query("select u from UserEntity u where u.username =:username")
    Optional<UserEntity> findUserByName(@Param("username") String username);
}
