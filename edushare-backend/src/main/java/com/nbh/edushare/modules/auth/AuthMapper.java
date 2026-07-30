package com.nbh.edushare.modules.auth;


import com.nbh.edushare.modules.auth.dto.request.RegisterRequest;
import com.nbh.edushare.modules.user.dto.command.CreateUserCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface AuthMapper {
    CreateUserCommand toCreateUserCommand(RegisterRequest request);
}