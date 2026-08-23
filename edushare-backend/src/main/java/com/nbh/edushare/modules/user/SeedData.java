package com.nbh.edushare.modules.user;

import com.nbh.edushare.modules.user.enums.UserRole;
import com.nbh.edushare.modules.user.pojo.Follow;
import com.nbh.edushare.modules.user.pojo.Profile;
import com.nbh.edushare.modules.user.pojo.User;
import com.nbh.edushare.modules.user.repository.FollowRepository;
import com.nbh.edushare.modules.user.repository.ProfileRepository;
import com.nbh.edushare.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
//@Profile({"dev", "local"}) // chỉ chạy ở dev/local, KHÔNG chạy ở prod
@Slf4j
public class SeedData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final FollowRepository followRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Random RANDOM = new Random();

    private static final String[] UNIVERSITIES = {
            "Đại học Mở TP.HCM", "Đại học Bách Khoa TP.HCM", "Đại học Khoa học Tự nhiên",
            "Đại học Kinh tế TP.HCM", "Đại học Công nghệ Thông tin"
    };

    private static final String[] FACULTIES = {
            "Công nghệ Thông tin", "Kỹ thuật Phần mềm", "Khoa học Máy tính",
            "Kinh tế", "Quản trị Kinh doanh"
    };

    @Override
    @Transactional
    public void run(String... args) {
        // tránh seed trùng nếu app restart nhiều lần
        if (userRepository.count() > 0) {
            log.info("Users already exist, skip seeding.");
            return;
        }

        log.info("Seeding users, profiles, follow relationships...");

        List<User> users = seedUsers(20);
        seedProfiles(users);
        seedFollows(users);

        log.info("Seeding done: {} users, {} follow relationships",
                users.size(), followRepository.count());

        log.info("VERIFY - profiles in DB: {}", profileRepository.count());
        log.info("VERIFY - follows in DB: {}", followRepository.count());
    }

    private List<User> seedUsers(int count) {
        List<User> users = new ArrayList<>();

        // 1 admin cố định để dễ test
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@edushare.vn");
        admin.setPasswordHash(passwordEncoder.encode("Admin@123"));
        admin.setFullName("Quản trị viên");
        admin.setUserRole(UserRole.ADMIN);
        users.add(admin);

        for (int i = 1; i <= count; i++) {
            User user = new User();
            user.setUsername("user" + i);
            user.setEmail("user" + i + "@edushare.vn");
            user.setPasswordHash(passwordEncoder.encode("User@123"));
            user.setFullName("Người dùng " + i);
            user.setUserRole(UserRole.USER);
            users.add(user);
        }

        return userRepository.saveAll(users);
    }

    private void seedProfiles(List<User> users) {
        List<Profile> profiles = new ArrayList<>();

        for (User user : users) {
            Profile profile = new Profile();
            profile.setUser(user);
            profile.setStudentId("235105" + String.format("%04d", RANDOM.nextInt(9999)));
            profile.setUniversity(UNIVERSITIES[RANDOM.nextInt(UNIVERSITIES.length)]);
            profile.setFaculty(FACULTIES[RANDOM.nextInt(FACULTIES.length)]);
            profile.setMajor("Kỹ thuật Phần mềm");
            profile.setClassName("DH23IT0" + (RANDOM.nextInt(9) + 1));
            profile.setAcademicYear("K23");
            profile.setCpa(BigDecimal.valueOf(2.5 + RANDOM.nextDouble() * 1.5)
                    .setScale(2, java.math.RoundingMode.HALF_UP));
            profile.setBio("Xin chào, mình là " + user.getFullName());
            profiles.add(profile);
        }

        profileRepository.saveAll(profiles);
    }

    private void seedFollows(List<User> users) {
        List<Follow> follows = new ArrayList<>();

        // bỏ admin ra, chỉ cho user thường follow nhau
        List<User> normalUsers = users.stream()
                .filter(u -> u.getUserRole() == UserRole.USER)
                .toList();

        for (User follower : normalUsers) {
            // mỗi user follow ngẫu nhiên 3-6 người khác (không tự follow chính mình)
            int followCount = 3 + RANDOM.nextInt(4);
            List<User> candidates = new ArrayList<>(normalUsers);
            candidates.remove(follower);
            java.util.Collections.shuffle(candidates);

            for (int i = 0; i < followCount && i < candidates.size(); i++) {
                User followee = candidates.get(i);

                Follow follow = new Follow();
                follow.setFollower(follower);
                follow.setFollowee(followee);
                follows.add(follow);
            }
        }

        followRepository.saveAll(follows);
    }
}