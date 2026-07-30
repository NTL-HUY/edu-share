package com.nbh.edushare.modules.user.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSimpleResponse {
    private Long id;
    private String username;
    private String fullName;
    private String avatarUrl;
}
