package com.jjbacsa.jjbacsabackend.inquiry.controller;

import com.jjbacsa.jjbacsabackend.etc.annotations.ValidationGroups;
import com.jjbacsa.jjbacsabackend.inquiry.dto.request.AnswerRequest;
import com.jjbacsa.jjbacsabackend.inquiry.dto.request.InquiryCursorRequest;
import com.jjbacsa.jjbacsabackend.inquiry.dto.request.InquiryRequest;
import com.jjbacsa.jjbacsabackend.inquiry.dto.response.InquiryResponse;
import com.jjbacsa.jjbacsabackend.inquiry.service.InquiryService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class InquiryController {

    private final InquiryService inquiryService;

    @ApiOperation(
            value = "Inquiry 조회",
            notes = "Inquiry를 조회합니다. 비밀글은 작성자만 조회 가능합니다.\n\n" +
                    "관리자의 경우 비밀글 여부 상관없이 조회 가능합니다.\n\n" +
                    "{\n\n" +
                    "       \"inquiryId\" : \"조회할 Inquiry의 id\"\"\n\n" +
                    "}", authorizations = @Authorization(value = "Bearer +accessToken"))
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "읽어온 Inquiry 정보",
                    response = InquiryResponse.class)
    })
    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value = "/inquiry/{inquiry-id}")
    public ResponseEntity<InquiryResponse> get(@ApiParam("조회할 Inquiry id") @PathVariable("inquiry-id") Long inquiryId) throws Exception {
        return new ResponseEntity<>(inquiryService.getInquiry(inquiryId), HttpStatus.OK);
    }

    @ApiOperation(
            value = "Inquiry 작성",
            notes = "Inquiry를 작성합니다. MediaType은 MULTIPART_FORM_DATA_VALUE를 선택해주세요.\n\n" +
                    "{\n\n" +
                    "       \"title\" : \"제목\"\n\n" +
                    "       \"content\" : \"내용\"\n\n" +
                    "       \"inquiryImages\" : \"문의 이미지\"\n\n" +
                    "       \"secret\" : \"비밀글 여부\"\n\n" +
                    "}", authorizations = @Authorization(value = "Bearer +accessToken"))
    @ApiResponses({
            @ApiResponse(code = 201,
                    message = "작성한 Inquiry 정보",
                    response = InquiryResponse.class)
    })
    @PreAuthorize("hasRole('NORMAL')")
    @PostMapping(value = "/inquiry", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<InquiryResponse> create(@Validated @ModelAttribute InquiryRequest inquiryRequest) throws Exception {
        return new ResponseEntity<>(inquiryService.create(inquiryRequest), HttpStatus.CREATED);
    }

    @ApiOperation(
            value = "Inquiry 수정",
            notes = "Inquiry를 수정합니다. MediaType은 MULTIPART_FORM_DATA_VALUE를 선택해주세요.\n\n" +
                    "{\n\n" +
                    "       \"inquiryId\" : \"수정할 Inquiry의 id\"\"\n\n" +
                    "       \"title\" : \"제목\"\n\n" +
                    "       \"content\" : \"내용\"\n\n" +
                    "       \"inquiryImages\" : \"문의 이미지\"\n\n" +
                    "       \"secret\" : \"비밀글 여부\"\n\n" +
                    "}", authorizations = @Authorization(value = "Bearer +accessToken"))
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "수정한 Inquiry 정보",
                    response = InquiryResponse.class)
    })
    @PreAuthorize("hasRole('NORMAL')")
    @PutMapping(value = "/inquiry/{inquiry-id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<InquiryResponse> modify(@Validated @ModelAttribute @RequestBody InquiryRequest inquiryRequest, @ApiParam("수정할 Inquiry Id") @PathVariable("inquiry-id") Long inquiryId) throws Exception {
        return new ResponseEntity<>(inquiryService.modify(inquiryRequest, inquiryId), HttpStatus.OK);
    }

    @ApiOperation(
            value = "Inquiry 삭제",
            notes = "Inquiry를 삭제합니다.\n\n" +
                    "{\n\n" +
                    "       \"inquiryId\" : \"삭제할 Inquiry의 id\"\"\n\n" +
                    "}", authorizations = @Authorization(value = "Bearer +accessToken"))
    @ApiResponses({
            @ApiResponse(code = 204,
                    message = "반환값 없음")
    })
    @PreAuthorize("hasRole('NORMAL')")
    @DeleteMapping(value = "/inquiry/{inquiry-id}")
    public ResponseEntity<Void> delete(@ApiParam("삭제할 Inquiry Id") @PathVariable("inquiry-id") Long inquiryId) throws Exception {
        inquiryService.delete(inquiryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(
            value = "Inquiry 답변 등록",
            notes = "Inquiry에 답변을 등록합니다.\n\n" +
                    "Admin 권한이 필요합니다.\n\n" +
                    "{\n\n" +
                    "       \"answer\" : \"Inquiry에 대한 답변\"\"\n\n" +
                    "}", authorizations = @Authorization(value = "Bearer +accessToken"))
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "답변한 Inquiry 정보",
                    response = InquiryResponse.class)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/admin/inquiry/{inquiry-id}")
    public ResponseEntity<InquiryResponse> answer(@Validated(ValidationGroups.Create.class) @RequestBody AnswerRequest answerRequest, @ApiParam("답변할 Inquiry Id") @PathVariable("inquiry-id") Long inquiryId) {
        return new ResponseEntity<>(inquiryService.addAnswer(answerRequest, inquiryId), HttpStatus.OK);
    }

    @ApiOperation(
            value = "전체 Inquiry 조회",
            notes = "전체 Inquiry를 조회합니다.\n\n" +
                    "커서 기반 페이징\n\n" +
                    "example : \n\n" +
                    "{\n\n" +
                    "       \"idCursor\" : \"조회한 마지막 Inquiry id, 첫 조회는 null\"\n\n" +
                    "       \"dateCursor\" : \"조회한 마지막 리뷰 createdAt, 첫 조회는 null\"\n\n" +
                    "       \"size\" : \"조회할 Inquiry의 개수\"\n\n" +
                    "}", authorizations = @Authorization(value = "Bearer +accessToken"))
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "조회한 Inquiry 정보",
                    response = InquiryResponse.class)
    })
    @GetMapping(value = "/inquiry")
    public ResponseEntity<Page<InquiryResponse>> getInquiries(@Validated InquiryCursorRequest inquiryCursorRequest) {
        return new ResponseEntity<>(inquiryService.getInquiries(inquiryCursorRequest), HttpStatus.OK);
    }

    @ApiOperation(
            value = "나의 Inquiry 조회",
            notes = "나의 Inquiry를 조회합니다.\n\n" +
                    "커서 기반 페이징\n\n" +
                    "example : \n\n" +
                    "{\n\n" +
                    "       \"idCursor\" : \"조회한 마지막 Inquiry id, 첫 조회는 null\"\n\n" +
                    "       \"dateCursor\" : \"조회한 마지막 리뷰 createdAt, 첫 조회는 null\"\n\n" +
                    "       \"size\" : \"조회할 Inquiry의 개수\"\n\n" +
                    "}", authorizations = @Authorization(value = "Bearer +accessToken"))
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "조회한 Inquiry 정보",
                    response = InquiryResponse.class)
    })
    @GetMapping(value = "/inquiry/me")
    public ResponseEntity<Page<InquiryResponse>> getMyInquiries(@Validated InquiryCursorRequest inquiryCursorRequest) throws Exception {
        return new ResponseEntity<>(inquiryService.getMyInquiries(inquiryCursorRequest), HttpStatus.OK);
    }

    @ApiOperation(
            value = "Inquiry 검색",
            notes = "Inquiry를 검색 합니다.\n\n" +
                    "커서 기반 페이징\n\n" +
                    "example : \n\n" +
                    "{\n\n" +
                    "       \"idCursor\" : \"조회한 마지막 Inquiry id, 첫 조회는 null\"\n\n" +
                    "       \"dateCursor\" : \"조회한 마지막 리뷰 createdAt, 첫 조회는 null\"\n\n" +
                    "       \"size\" : \"조회할 Inquiry의 개수\"\n\n" +
                    "       \"searchWord\" : \"검색어\"\n\n" +
                    "}", authorizations = @Authorization(value = "Bearer +accessToken"))
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "조회한 Inquiry 정보",
                    response = InquiryResponse.class)
    })
    @GetMapping(value = "/inquiry/search/{search-word}")
    public ResponseEntity<Page<InquiryResponse>> searchInquiries(@Validated InquiryCursorRequest inquiryCursorRequest, @ApiParam("검색어") @PathVariable(name = "search-word") String searchWord) {
        return new ResponseEntity<>(inquiryService.searchInquiries(inquiryCursorRequest, searchWord), HttpStatus.OK);
    }

    @ApiOperation(
            value = "나의 Inquiry 검색",
            notes = "나의 Inquiry를 검색합니다.\n\n" +
                    "커서 기반 페이징\n\n" +
                    "example : \n\n" +
                    "{\n\n" +
                    "       \"idCursor\" : \"조회한 마지막 Inquiry id, 첫 조회는 null\"\n\n" +
                    "       \"dateCursor\" : \"조회한 마지막 리뷰 createdAt, 첫 조회는 null\"\n\n" +
                    "       \"size\" : \"조회할 Inquiry의 개수\"\n\n" +
                    "       \"searchWord\" : \"검색어\"\n\n" +
                    "}", authorizations = @Authorization(value = "Bearer +accessToken"))
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "조회한 Inquiry 정보",
                    response = InquiryResponse.class)
    })
    @GetMapping(value = "/inquiry/search/me/{search-word}")
    public ResponseEntity<Page<InquiryResponse>> searchMyInquiries(@Validated InquiryCursorRequest inquiryCursorRequest, @ApiParam("검색어") @PathVariable(name = "search-word") String searchWord) throws Exception {
        return new ResponseEntity<>(inquiryService.searchMyInquiries(inquiryCursorRequest, searchWord), HttpStatus.OK);
    }

}
