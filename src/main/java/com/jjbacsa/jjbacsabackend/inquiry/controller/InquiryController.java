package com.jjbacsa.jjbacsabackend.inquiry.controller;

import com.jjbacsa.jjbacsabackend.inquiry.dto.request.AnswerRequest;
import com.jjbacsa.jjbacsabackend.inquiry.dto.request.InquiryRequest;
import com.jjbacsa.jjbacsabackend.inquiry.dto.response.InquiryResponse;
import com.jjbacsa.jjbacsabackend.inquiry.service.InquiryService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class InquiryController {

    private final InquiryService inquiryService;

    @ApiOperation(
            value = "Inquiry 조회",
            notes = "Inquiry를 조회합니다. 비밀번호가 있을 시 내용과 답변을 볼 수 없습니다.\n\n" +
                    "관리자의 경우 비밀번호 여부 없이 조회 가능합니다.\n\n" +
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
            value = "Inquiry 조회",
            notes = "Inquiry를 조회합니다. 비밀번호가 있다면 비밀번호를 확인합니다.\n\n" +
                    "{\n\n" +
                    "       \"inquiryId\" : \"조회할 Inquiry의 id\"\"\n\n" +
                    "       \"secret\" : \"조회할 Inquiry의 비밀번호\"\"\n\n" +
                    "}", authorizations = @Authorization(value = "Bearer +accessToken"))
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "읽어온 Inquiry 정보",
                    response = InquiryResponse.class)
    })
    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value = "/inquiry/{inquiry-id}/secret")
    public ResponseEntity<InquiryResponse> get(@ApiParam("조회할 Inquiry id") @PathVariable("inquiry-id") Long inquiryId, @RequestParam(value = "secret") String secret) throws Exception {
        return new ResponseEntity<>(inquiryService.getInquiryWithSecret(inquiryId, secret), HttpStatus.OK);
    }

    @ApiOperation(
            value = "Inquiry 작성",
            notes = "Inquiry를 작성합니다.\n\n" +
                    "{\n\n" +
                    "       \"title\" : \"제목\"\n\n" +
                    "       \"content\" : \"내용\"\n\n"+
                    "       \"secret\" : \"비밀번호\"\n\n"+
                    "}", authorizations = @Authorization(value = "Bearer +accessToken"))
    @ApiResponses({
            @ApiResponse(code = 201,
                    message = "작성한 Inquiry 정보",
                    response = InquiryResponse.class)
    })
    @PreAuthorize("hasRole('NORMAL')")
    @PostMapping(value = "/inquiry")
    public ResponseEntity<InquiryResponse> create(@RequestBody InquiryRequest inquiryRequest) throws Exception {
        // Validate
        return new ResponseEntity<>(inquiryService.create(inquiryRequest), HttpStatus.CREATED);
    }

    @ApiOperation(
            value = "Inquiry 수정",
            notes = "Inquiry를 수정합니다. 비밀번호는 수정할 수 없습니다.\n\n" +
                    "{\n\n" +
                    "       \"inquiryId\" : \"수정할 Inquiry의 id\"\"\n\n" +
                    "       \"title\" : \"제목\"\n\n" +
                    "       \"content\" : \"내용\"\n\n"+
                    "}", authorizations = @Authorization(value = "Bearer +accessToken"))
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "수정한 Inquiry 정보",
                    response = InquiryResponse.class)
    })
    @PreAuthorize("hasRole('NORMAL')")
    @PatchMapping(value = "/inquiry/{inquiry-id}")
    public ResponseEntity<InquiryResponse> modify(@RequestBody InquiryRequest inquiryRequest, @ApiParam("수정할 Inquiry Id") @PathVariable("inquiry-id") Long inquiryId) throws Exception {
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
    public ResponseEntity<InquiryResponse> answer(@RequestBody AnswerRequest answerRequest, @ApiParam("답변할 Inquiry Id") @PathVariable("inquiry-id") Long inquiryId){
        return new ResponseEntity<>(inquiryService.addAnswer(answerRequest, inquiryId), HttpStatus.OK);
    }
}
