package com.example.todo.userapi.api;

import com.example.todo.auth.TokenUserInfo;
import com.example.todo.exception.DuplicatedEmailException;
import com.example.todo.exception.NoRegisteredArgumentsException;
import com.example.todo.userapi.dto.request.LoginRequestDTO;
import com.example.todo.userapi.dto.request.UserRequestSignUpDTO;
import com.example.todo.userapi.dto.response.LoginResposeDTO;
import com.example.todo.userapi.dto.response.UserSignUpResponseDTO;
import com.example.todo.userapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

//    이메일 중복확인 요청 처리
//    GET : /api/auth/check?email=zzz@aaa.com
    @GetMapping("/check")
public ResponseEntity<?> check(String email) {
        if(email.trim().equals("")){
            return ResponseEntity.badRequest().body("이메일이 없습니다");
        }

        boolean resultFlag = userService.isDuplicate(email);
        log.info("{}-중복됨> {}", email,resultFlag);
        return ResponseEntity.ok().body(resultFlag);

    }


//    회원가입 요청처리
//    POST : /api/auth
    @PostMapping
    public ResponseEntity<?> signUp
    (@Validated @RequestPart("user") UserRequestSignUpDTO dto,
                // @RequestBody 가 변경 됨
                @RequestPart(value ="profileImage", required = false) MultipartFile profileImg,
                BindingResult result) {


        log.info("/api/auth POST - {}", dto);

        if(result.hasErrors()){
            log.warn(result.toString());
            return ResponseEntity.badRequest().body(result.getFieldError());
        }
        try {
                String uploadFilePath = null;
            if(profileImg != null)
            {
                String originalFilename = profileImg.getOriginalFilename();
                log.info("attached file name : {}", originalFilename);
                uploadFilePath = userService.uploadProfileImage(profileImg);
            }

            UserSignUpResponseDTO responseDTO = userService.create(dto,uploadFilePath);
            return ResponseEntity.ok().body(responseDTO);

        } catch (NoRegisteredArgumentsException e) {
            log.warn("필수 가입 정보를 전달받지 못했습니다.");

            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (DuplicatedEmailException e) {
            log.warn("이메일 중복입니다!");
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (Exception e) {
//            파일에 대한 에러가 생겼을 때 뜨는 에러 결정.
            log.warn("기타예외 발생");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }

    }


//    로그인 요청처리

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@Validated @RequestBody LoginRequestDTO dto) {

        try {
            LoginResposeDTO resposeDTO = userService.authenticated(dto);
            return ResponseEntity.ok().body(resposeDTO);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }



    }

//   일반회원을 프리미엄회원으로 승격하는 요청 처리
    @PutMapping("/promote")
    @PreAuthorize("hasRole('ROLE_COMMON')") //해당 권한을 가졌을 때만 이 메서드가 실행되도록 만듦.
    public ResponseEntity<?> promote(
            //권한검사(해당 권한이 아니면 인가처리 거부 403 코드 리턴)
            @AuthenticationPrincipal TokenUserInfo userInfo
            ) {
        log.info("/api/auth/promote PUT!");

        try {
//            권한이 바뀌면 토큰도 재생성해서 클라이언트에게 보내줘야 함.
          LoginResposeDTO resposeDTO
                  = userService.promoteToPremium(userInfo);
            return ResponseEntity.ok().body(resposeDTO);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build(); //500번에러 처리
        }


    }



}
