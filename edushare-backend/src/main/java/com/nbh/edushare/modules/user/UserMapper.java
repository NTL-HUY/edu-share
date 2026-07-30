package com.nbh.edushare.modules.user;


import com.nbh.edushare.modules.user.dto.command.CreateUserCommand;
import com.nbh.edushare.modules.user.dto.response.UserAuthInfo;
import com.nbh.edushare.modules.user.dto.response.UserSimpleResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface UserMapper {
    UserSimpleResponse toUserSimpleResponse(User user);
    User toUser(CreateUserCommand command);
    UserAuthInfo toUserAuthInfo(User user);
}
