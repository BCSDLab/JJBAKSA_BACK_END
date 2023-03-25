package com.jjbacsa.jjbacsabackend.follow.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jjbacsa.jjbacsabackend.user.dto.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowRequestResponse {

    private Long id;
    private Date createdAt;
    private UserResponse user;
    private UserResponse follower;
}
