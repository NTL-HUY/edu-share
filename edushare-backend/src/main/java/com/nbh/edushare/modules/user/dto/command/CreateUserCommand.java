package com.nbh.edushare.modules.user.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateUserCommand {
    private String username;
    private String email;
    private String password;
    private String fullName;
}