package com.example.todo.userapi.service;

import com.example.todo.userapi.dto.request.UserRequestSignUpDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class UserServiceTest {

    @Autowired
    UserService userService;

    @Test
    @DisplayName("중복된 이메일로 회원가입하면 Run~exception 발생해야한다")
    void validateEmailTest () {

        //given
        String email = "abcD1234@abc.com";
        UserRequestSignUpDTO dto = UserRequestSignUpDTO.builder()
                .email(email)
                .password("11111")
                .userName("ame")
                .build();
        //when

        //then
//        에러발생테스트, 에러가 발생해야 테스트 통과
//        param1 : 어떤 에러가 발생할지 에러클래스 적음
//        param2: 에러가 발생하는 상황전달
        assertThrows(RuntimeException.class,
                () -> {
                    userService.create(dto, "");
                });

    }

}