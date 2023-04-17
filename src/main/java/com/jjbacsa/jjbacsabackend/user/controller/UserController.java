package com.jjbacsa.jjbacsabackend.user.controller;

import com.jjbacsa.jjbacsabackend.etc.annotations.ValidationGroups;
import com.jjbacsa.jjbacsabackend.etc.dto.Token;
import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.user.dto.EmailRequest;
import com.jjbacsa.jjbacsabackend.user.dto.UserRequest;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponseWithFollowedType;
import com.jjbacsa.jjbacsabackend.user.service.InternalEmailService;
import com.jjbacsa.jjbacsabackend.user.service.UserService;
import com.jjbacsa.jjbacsabackend.user.serviceImpl.OAuth2UserServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.net.URI;

@RequiredArgsConstructor
@RestController
@Validated
public class UserController {
    private final UserService userService;
    private final OAuth2UserServiceImpl oAuth2UserService;
    private final InternalEmailService emailService;

    @ApiOperation(
            value = "회원가입",
            notes = "회원가입을 진행합니다.\n\n" +
                    "필요한 필드\n\n\t" +
                    "{\n\n     " +
                    "account : 유저 계정(1~20글자의 영문자 및 숫자),\n\n     " +
                    "password : 유저 패스워드(영문자, 숫자, 특수문자를 포함하는 8~16의 문자열),\n\n     " +
                    "email : 유저 이메일(차후 인증 추가)\n\n\t}")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses({
            @ApiResponse(code = 201,
                    message = "생성된 유저 정보",
                    response = UserResponse.class)
    })
    @PostMapping(value = "/user")
    public ResponseEntity<UserResponse> register(@Validated(ValidationGroups.Create.class)
                                                 @RequestBody UserRequest request) throws Exception {
        return new ResponseEntity<>(userService.register(request), HttpStatus.CREATED);
    }

    @ApiOperation(
            value = "아이디 중복 확인",
            notes = "아이디 중복을 확인합니다." +
                    "\n\n\taccount : 유저 계정(1~20글자의 영문자 및 숫자)")
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "OK",
                    response = String.class)
    })
    @GetMapping(value = "/user/exists")
    public ResponseEntity<String> checkDuplicateAccount(
            @Pattern(regexp = "^[a-zA-z가-힣0-9]{1,20}$", message = "닉네임에 특수문자와 초성은 불가능합니다.")
            @RequestParam String account) throws Exception {
        return new ResponseEntity<>(userService.checkDuplicateAccount(account), HttpStatus.OK);
    }

    @ApiOperation(
            value = "로그인",
            notes = "로그인을 진행합니다.\n\n" +
                    "필요한 필드\n\n\t" +
                    "{\n\n     " +
                    "account : 유저 계정(1~20글자의 영문자 및 숫자),\n\n     " +
                    "password : 유저 패스워드(영문자, 숫자, 특수문자를 포함하는 8~16의 문자열),\n\n" +
                    "\t}")
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "Access Token, Refresh Token 반환",
                    response = Token.class)
    })
    @PostMapping(value = "/user/login")
    public ResponseEntity<Token> login(@Validated(ValidationGroups.Login.class)
                                       @RequestBody UserRequest request) throws Exception {
        return new ResponseEntity<>(userService.login(request), HttpStatus.OK);
    }

    @ApiOperation(
            value = "본인 정보 확인",
            notes = "로그인 유저 정보 확인\n\n" +
                    "필요 헤더\n\n" +
                    "\tAuthorization : access token",
            authorizations = @Authorization(value = "Bearer + accessToken"))
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "로그인한 유저의 정보 반환",
                    response = UserResponse.class)
    })
    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping(value = "/user/me")
    public ResponseEntity<UserResponse> getMe() throws Exception {
        return new ResponseEntity<>(userService.getLoginUser(), HttpStatus.OK);
    }

    @ApiOperation(
            value = "토큰 재발급",
            notes = "accessToken을 재발급합니다.\n\n" +
                    "필요 헤더\n\n" +
                    "\tRefreshToken : Bearer + Refresh token",
            authorizations = @Authorization(value = "Bearer + refreshToken"))
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "Access Token, Refresh Token 발급",
                    response = Token.class)
    })
    @GetMapping("/user/refresh")
    public ResponseEntity<Token> refresh() throws Exception {
        return new ResponseEntity<>(userService.refresh(), HttpStatus.OK);
    }

    @ApiOperation(
            value = "유저 목록 검색",
            notes = "keyword가 포함된 유저 닉네임 검색\n\n\t" +
                    "keyword : 검색에 사용할 문자열 (Not Null)\n\n\t" +
                    "pageSize : 한 번에 출력할 결과 갯수(1~100, Default = 20)\n\n\t" +
                    "cursor : 마지막으로 조회한 유저의 id\n\n" +
                    "keyword 제외하고 모두 null을 주면 검색 첫페이지가 반환됩니다."
    )
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "검색된 유저 리스트 반환",
                    response = UserResponse.class,
                    responseContainer = "Page")
    })
    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping("/users")
    public ResponseEntity<Page<UserResponseWithFollowedType>> searchUsers(
            @Size(min = 1, max = 20, message = "닉네임은 1~20글자까지 검색할 수 있습니다.") @RequestParam String keyword,
            @ApiParam("가져올 데이터 수(1~100)") @Range(min = 0, max = 100, message = "올바르지 않은 값입니다.")
            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @ApiParam("마지막으로 조회한 유저의 id")
            @RequestParam(required = false) Long cursor) throws Exception {
        return new ResponseEntity<>(userService.searchUsers(keyword, pageSize, cursor), HttpStatus.OK);
    }

    @ApiOperation(
            value = "유저 검색",
            notes = "\n\n\tid : 검색할 유저의 id"
    )
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "검색된 유저 정보 반환",
                    response = UserResponse.class)
    })
    @GetMapping("/user/{id}")
    public ResponseEntity<UserResponse> getAccountInfo(@ApiParam("검색할 유저의 id") @PathVariable Long id) throws Exception {
        return new ResponseEntity<>(userService.getAccountInfo(id), HttpStatus.OK);
    }

    @ApiOperation(
            value = "유저 정보 변경",
            notes = "유저 정보 변경\n\n" +
                    "필요 헤더\n\n" +
                    "\tAuthorization : Bearer + access token\n\n" +
                    "필요한 필드\n\n" +
                    "\t{\n\n     " +
                    "password : 변경할 유저 패스워드(영문자, 숫자, 특수문자를 포함하는 8~16의 문자열)(차후 인증 적용),\n\n     " +
                    "nickname : 변경할 유저 닉네임(영문자, 한글, 숫자로 이루어진 1~20글자의 문자열),\n\n     " +
                    "email : 변경할 유저 계정 (차후 인증 적용),\n\n" +
                    "\t}",
            authorizations = @Authorization(value = "Bearer + refreshToken"))
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "변경된 유저 정보",
                    response = UserResponse.class)
    })
    @PreAuthorize("hasRole('NORMAL')")
    @PatchMapping("/user/me")
    public ResponseEntity<UserResponse> modifyUser(@Validated(ValidationGroups.Update.class)
                                                   @RequestBody UserRequest request) throws Exception {
        return new ResponseEntity<>(userService.modifyUser(request), HttpStatus.OK);
    }

    @ApiOperation(
            value = "회원 탈퇴",
            notes = "회원 탈퇴\n\n" +
                    "필요 헤더\n\n" +
                    "\tAuthorization : Bearer + access token"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses({
            @ApiResponse(code = 204,
                    message = "반환값 없음")
    })
    @PreAuthorize(("hasRole('NORMAL')"))
    @DeleteMapping("/user/me")
    public ResponseEntity<Void> withdraw() throws Exception {
        userService.withdraw();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(
            value = "아이디 찾기 인증 이메일 발송",
            notes = "아이디 찾기 인증 이메일 발송\n\n" +
                    "\n\n\temail : 인증 받을 이메일"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "OK")
    })
    @PostMapping("/user/email/account")
    public ResponseEntity<String> sendAuthEmailAccount(@Email(message = "이메일은 형식을 지켜야 합니다.")
                                                       @RequestParam String email) throws Exception {
        userService.sendAuthEmailCode(email);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @ApiOperation(
            value = "비밀번호 찾기 인증 이메일 발송",
            notes = "비밀번호 찾기 인증 이메일 발송\n\n" +
                    "\n\n\taccount : 인증 받을 아이디" +
                    "\n\n\temail : 인증 받을 이메일"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "OK")
    })
    @PostMapping("/user/email/password")
    public ResponseEntity<String> sendAuthEmailPassword(@RequestParam String account,
                                                        @Email(message = "이메일은 형식을 지켜야 합니다.")
                                                        @RequestParam String email) throws Exception {
        userService.sendAuthEmailCode(account, email);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @ApiOperation(
            value = "회원가입 인증 이메일 발송",
            notes = "회원가입 인증 이메일 발송\n\n" +
                    "\n\n\temail : 인증 받을 이메일"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "OK")
    })
    @PostMapping("/user/authenticate")
    public ResponseEntity<String> sendAuthEmailLink(@Email(message = "이메일은 형식을 지켜야 합니다.")
                                                    @RequestParam String email) throws Exception {
        userService.sendAuthEmailLink(email);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }


    @ApiOperation(
            value = "아이디 찾기",
            notes = "아이디 찾기\n\n\t" +
                    "email : 아이디를 찾을 이메일 - 메일 받은 주소\n\n\t" +
                    "code : 인증 코드"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "아이디 찾기",
                    response = UserResponse.class)
    })
    @GetMapping("user/account")
    public ResponseEntity<UserResponse> findAccount(@Email(message = "이메일은 형식을 지켜야 합니다.")
                                                    @RequestParam String email,
                                                    @RequestParam String code) throws Exception {

        return new ResponseEntity<>(userService.findAccount(email, code), HttpStatus.OK);
    }

    @ApiOperation(
            value = "비밀번호 찾기",
            notes = "비밀번호 찾기 이메일 발송\n\n" +
                    "필요한 필드\n\n" +
                    "\t{\n\n     " +
                    "account : 비밀번호 찾을 계정\n\n     " +
                    "email : 비밀번호 찾을 이메일 - 메일 받은 주소\n\n     " +
                    "code : 인증 코드\n\n     " +
                    "\t}"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "비밀번호 찾기",
                    response = UserResponse.class)
    })
    @PostMapping("user/password")
    public ResponseEntity<String> findPassword(@Validated(ValidationGroups.Update.class)
                                               @RequestBody EmailRequest request) throws Exception {
        return new ResponseEntity<>(userService.findPassword(request), HttpStatus.OK);
    }

    @ApiOperation(
            value = "비밀번호 변경",
            notes = "비밀번호 변경\n\n" +
                    "필요 헤더" +
                    "\n\n\tAuthorization : Bearer + access token\n\n" +
                    "필요한 필드" +
                    "\n\n\tpassword : 변경할 유저 패스워드(영문자, 숫자, 특수문자를 포함하는 8~16의 문자열)\n\n\t",
            authorizations = @Authorization(value = "Bearer + accessToken"))
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "변경된 유저 정보",
                    response = UserResponse.class)
    })
    @PreAuthorize("hasRole('NORMAL')")
    @PatchMapping("/user/password")
    public ResponseEntity<UserResponse> modifyPassword(@Pattern(regexp = "(?=[0-9a-zA-z~!@#$%^&*()\\-_=+]*[0-9])(?=[0-9a-zA-z~!@#$%^&*()\\-_=+]*[a-zA-z])(?=[0-9a-zA-z~!@#$%^&*()\\-_=+]*[~!@#$%^&*()\\-_=+]).{8,16}",
            groups = {ValidationGroups.Update.class}, message = "올바른 형식의 비밀번호가 아닙니다.")
                                                       @RequestParam String password) throws Exception {
        return new ResponseEntity<>(userService.modifyPassword(password), HttpStatus.OK);
    }

    @ApiOperation(
            value = "회원 프로필 사진 수정",
            notes = "회원 프로필 사진 수정\n\n" +
                    "필요 헤더\n\n" +
                    "\tAuthorization : Bearer + access token\n\n" +
                    "필요한 필드\n\n" +
                    "\t프로필 사진 : 파일 업로드\n\n" +
                    "\t업로드할 사진을 올리지 않을 시 기본 프로필로 변경"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "변경된 유저 정보")
    })
    @PreAuthorize("hasRole('NORMAL')")
    @PatchMapping(value = "/user/profile")
    public ResponseEntity<UserResponse> modifyProfile(@RequestPart(value = "profile", required = false)
                                                      MultipartFile profile) throws Exception {
        return new ResponseEntity<>(userService.modifyProfile(profile), HttpStatus.OK);
    }

    @ApiOperation(
            value = "소셜 로그인",
            notes = "소셜 로그인\n\n" +
                    "필요 헤더\n\n" +
                    "\n\n\tAuthorization : 클라이언트 측에서 발급 받은 token" +
                    "\n\nKAKAO, NAVER : Access Token" +
                    "\n\nAPPLE, GOOGLE : Id Token"
    )
    @PostMapping(value = "/login/{sns-type}")
    public ResponseEntity<Token> snsLogin(
            @PathVariable(name = "sns-type") OAuthType oAuthType
    ) throws Exception {
        return new ResponseEntity<>(oAuth2UserService.oAuthLoginByToken(oAuthType), HttpStatus.OK);
    }

    @ApiOperation(
            value = "회원가입 이메일 인증용 API",
            notes = "회원가입 이메일 인증\n\n" +
                    "\n\n\taccess_token : 이메일에 전송한 링크에 포함된 access_token" +
                    "\n\n\trefresh_token : 이메일에 전송한 링크에 포함된 refresh_token"
    )
    @ResponseStatus(HttpStatus.MOVED_PERMANENTLY)
    @GetMapping("/user/check-email")
    public ResponseEntity<?> authenticate(
            @RequestParam(value = "access_token") String accessToken,
            @RequestParam(value = "refresh_token") String refreshToken) throws Exception {

        HttpHeaders httpHeaders = new HttpHeaders();
        try {
            httpHeaders.setLocation(userService.authEmail(accessToken, refreshToken));
        } catch (RequestInputException exception) {
            httpHeaders.setLocation(URI.create("../email-error"));
        }

        return new ResponseEntity<>(httpHeaders, HttpStatus.MOVED_PERMANENTLY);
    }

    @ApiIgnore
    @GetMapping("/email-error")
    public ModelAndView getEmailErrorPage() {
        return new ModelAndView("authenticate-failed");
    }

    @ApiOperation(
            value = "닉네임 변경",
            notes = "닉네임 변경\n\n" +
                    "필요 헤더" +
                    "\n\n\tAuthorization : Bearer + access token\n\n" +
                    "필요한 필드" +
                    "\n\n\tnickname : 변경할 유저 닉네임(영문자, 한글, 숫자로 이루어진 1~20글자의 문자열)\n\n\t",
            authorizations = @Authorization(value = "Bearer + accessToken"))
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "변경된 유저 정보",
                    response = UserResponse.class)
    })
    @PreAuthorize("hasRole('NORMAL')")
    @PatchMapping("/user/nickname")
    public ResponseEntity<UserResponse> modifyNickname(@Pattern(regexp = "^[a-zA-z가-힣0-9]{1,20}$",
            groups = {ValidationGroups.Update.class}, message = "닉네임에 특수문자와 초성은 불가능합니다.")
                                                       @RequestParam String nickname) throws Exception {
        return new ResponseEntity<>(userService.modifyNickname(nickname), HttpStatus.OK);
    }


}
