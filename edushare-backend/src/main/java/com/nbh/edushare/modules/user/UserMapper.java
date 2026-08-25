package com.nbh.edushare.modules.user;


import com.nbh.edushare.modules.user.dto.command.CreateUserCommand;
import com.nbh.edushare.modules.user.dto.request.UpdateProfileRequest;
import com.nbh.edushare.modules.user.dto.response.ProfileResponse;
import com.nbh.edushare.modules.user.dto.response.UserAuthInfo;
import com.nbh.edushare.modules.user.dto.response.UserSimpleResponse;
import com.nbh.edushare.modules.user.pojo.Profile;
import com.nbh.edushare.modules.user.pojo.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
interface UserMapper {
    UserSimpleResponse toUserSimpleResponse(User user);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User toUser(CreateUserCommand command);

    @Mapping(target = "userId" ,source = "user.id")
    ProfileResponse toProfileResponse(Profile profile);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "fullName", source = "user.fullName")
    @Mapping(target = "avatarUrl", source = "user.avatarUrl")
    @Mapping(target = "isFamous", source = "user.isFamous")
    @Mapping(target = "studentId", source = "profile.studentId")
    @Mapping(target = "university", source = "profile.university")
    @Mapping(target = "faculty", source = "profile.faculty")
    @Mapping(target = "major", source = "profile.major")
    @Mapping(target = "className", source = "profile.className")
    @Mapping(target = "academicYear", source = "profile.academicYear")
    @Mapping(target = "cpa", source = "profile.cpa")
    @Mapping(target = "bio", source = "profile.bio")
    @Mapping(target = "coverUrl", source = "profile.coverUrl")
    @Mapping(target = "isMe", source = "isMe")
    @Mapping(target = "isFollowing", source = "isFollowing")
    ProfileResponse toProfileResponse(User user, Profile profile, boolean isMe, boolean isFollowing);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromRequest(UpdateProfileRequest request, @MappingTarget User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProfileFromRequest(UpdateProfileRequest request, @MappingTarget Profile profile);
}
