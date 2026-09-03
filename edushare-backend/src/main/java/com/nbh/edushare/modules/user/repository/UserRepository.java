package com.nbh.edushare.modules.user.repository;

import com.nbh.edushare.modules.user.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    <T> Optional<T> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail, Class<T> type);

    Optional<User> findByUsernameOrEmail(String username, String email);

    <T> Optional<T> findProjectedById(Long id, Class<T> type);

}
