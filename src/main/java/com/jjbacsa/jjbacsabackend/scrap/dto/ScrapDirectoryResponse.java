package com.jjbacsa.jjbacsabackend.scrap.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
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
public class ScrapDirectoryResponse {

    private Long id;
    private Date createdAt;
    private Date updatedAt;
    private String name;
}
