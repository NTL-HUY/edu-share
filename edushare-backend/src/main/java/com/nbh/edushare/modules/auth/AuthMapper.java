package com.nbh.edushare.modules.auth;


import com.nbh.edushare.modules.auth.dto.request.RegisterRequest;
import com.nbh.edushare.modules.auth.security.AuthenticatedUser;
import com.nbh.edushare.modules.user.dto.command.CreateUserCommand;
import com.nbh.edushare.modules.user.dto.response.UserAuthInfo;
import com.nbh.edushare.modules.user.dto.response.UserSimpleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
interface AuthMapper {
    CreateUserCommand toCreateUserCommand(RegisterRequest request);
}