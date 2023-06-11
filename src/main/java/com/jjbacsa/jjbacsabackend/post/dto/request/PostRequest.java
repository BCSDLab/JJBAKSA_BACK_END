package com.jjbacsa.jjbacsabackend.post.dto.request;

import com.jjbacsa.jjbacsabackend.etc.annotations.IsValidListSize;
import com.jjbacsa.jjbacsabackend.etc.annotations.ValidationGroups;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PostRequest {

    @ApiModelProperty(example = "title")
    @NotNull(groups = {ValidationGroups.AdminCreate.class})
    private String title;

    @ApiModelProperty(example = "content")
    @NotNull(groups = {ValidationGroups.AdminCreate.class})
    private String content;

    @IsValidListSize(max = 5, message = "공지사항 이미지는 최대 5개 입니다.")
    private List<MultipartFile> postImages;
}
