package com.nbh.edushare.modules.user.repository;

import com.nbh.edushare.modules.user.dto.response.UserSimpleResponse;
import com.nbh.edushare.modules.user.pojo.Follow;
import com.nbh.edushare.modules.user.pojo.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    long countByFollowee_Id(Long followeeId);
    boolean existsByFollowerIdAndFolloweeId(Long followerId, Long followeeId);
    Optional<Follow> findByFollowerIdAndFolloweeId(Long followerId, Long followeeId);



    // Lấy danh sách những người đang theo dõi user này (Followers)
    @Query("SELECT f.follower FROM Follow f WHERE f.followee.id = :userId")
    Page<User> findFollowersByUserId(@Param("userId") Long userId, Pageable pageable);

    // Lấy danh sách những người mà user này đang theo dõi (Following)
    @Query("SELECT f.followee FROM Follow f WHERE f.follower.id = :userId")
    Page<User> findFollowingByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("Select f.follower.id From Follow f where f.followee.id = :followeeId")
    List<Long> findFollowersIdByFollowee_Id(@Param("followeeId") Long followeeId);

    @Query("""
        SELECT f.followee.id FROM Follow f 
        WHERE f.follower.id = :userId 
        AND f.followee.isFamous = true
    """)
    List<Long> findFamousFolloweeIds(@Param("userId") Long userId);

    @Query("""
        SELECT f.followee.id FROM Follow f 
        WHERE f.follower.id = :userId 
          AND (f.followee.isFamous = false)
    """)
    List<Long> findNormalFolloweeIds(@Param("userId") Long userId);

}