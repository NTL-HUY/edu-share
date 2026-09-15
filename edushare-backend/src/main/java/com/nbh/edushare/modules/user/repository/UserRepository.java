package com.nbh.edushare.modules.user.repository;

import com.nbh.edushare.modules.user.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    <T> Optional<T> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail, Class<T> type);

    Optional<User> findByUsernameOrEmail(String username, String email);

    <T> Optional<T> findProjectedById(Long id, Class<T> type);
    List<Long> findIdByIsFamousTrue();

    @Modifying
    @Query("UPDATE User u SET u.isFamous = :isFamous WHERE u.id IN :ids")
    @Transactional
    int bulkSetFamous(@Param("ids")Collection <Long> ids, @Param("isFamous") Boolean isFamous);
}
