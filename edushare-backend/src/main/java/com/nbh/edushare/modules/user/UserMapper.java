package com.nbh.edushare.modules.user;


import com.nbh.edushare.modules.user.dto.command.CreateUserCommand;
import com.nbh.edushare.modules.user.dto.request.UpdateProfileRequest;
import com.nbh.edushare.modules.user.dto.response.ProfileResponse;
import com.nbh.edushare.modules.user.dto.response.UserAuthInfo;
import com.nbh.edushare.modules.user.dto.response.UserSimpleResponse;
import com.nbh.edushare.modules.user.pojo.Profile;
import com.nbh.edushare.modules.user.pojo.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
interface UserMapper {
    UserSimpleResponse toUserSimpleResponse(User user);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User toUser(CreateUserCommand command);

    @Mapping(target = "userId" ,source = "user.id")
    ProfileResponse toProfileResponse(Profile profile);
    void updateProfileFromRequest(UpdateProfileRequest request, @MappingTarget Profile profile);
}
