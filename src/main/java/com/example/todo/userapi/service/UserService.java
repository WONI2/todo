package com.example.todo.userapi.service;

import com.example.todo.exception.DuplicatedEmailException;
import com.example.todo.exception.NoRegisteredArgumentsException;
import com.example.todo.userapi.dto.request.UserRequestSignUpDTO;
import com.example.todo.userapi.dto.response.UserSignUpResponseDTO;
import com.example.todo.userapi.entity.User;
import com.example.todo.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder; //비밀번호 암호화하기 위한 인코더

//    회원가입 처리
    public UserSignUpResponseDTO create(final UserRequestSignUpDTO dto)
            throws RuntimeException{

        if(dto == null) {
            throw new NoRegisteredArgumentsException("가입정보가 없습니다");
        }
        String email = dto.getEmail();

        if (userRepository.existsByEmail(email)){
            log.warn("이메일이 중복됨 {}",email);
            throw new DuplicatedEmailException("중복된 이메일입니다");
        }
//        save 전에 패스워드 인코딩
        String encoded = encoder.encode(dto.getPassword());
        dto.setPassword(encoded);

//   유저 엔터티로 변환
        User user = dto.toEntity();

        User saved = userRepository.save(user);
        log.info("회원가입 정상 수행 - saved user - {}", saved );

        return new UserSignUpResponseDTO(saved);


    }

//이메일 실시간 검증
    public boolean isDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }
}
