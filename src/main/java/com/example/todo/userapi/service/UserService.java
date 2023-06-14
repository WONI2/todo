package com.example.todo.userapi.service;

import com.example.todo.auth.TokenProvider;
import com.example.todo.auth.TokenUserInfo;
import com.example.todo.exception.DuplicatedEmailException;
import com.example.todo.exception.NoRegisteredArgumentsException;
import com.example.todo.userapi.dto.request.LoginRequestDTO;
import com.example.todo.userapi.dto.request.UserRequestSignUpDTO;
import com.example.todo.userapi.dto.response.LoginResposeDTO;
import com.example.todo.userapi.dto.response.UserSignUpResponseDTO;
import com.example.todo.userapi.entity.Role;
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
    private final TokenProvider tokenProvider;

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


//    회원인증
    public LoginResposeDTO authenticated(final LoginRequestDTO dto) {
//        이메일을 통해 회원정보 조회
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(
                        () -> new RuntimeException("가입된 회원이 아닙니다")
                );
//        패스워드 검증
        String rawPassword = dto.getPassword(); //입력한 비밀번호
        String encodedPassword = user.getPassword(); //db에 저장된 비밀번호
        if(!encoder.matches(rawPassword, encodedPassword)){
            throw new RuntimeException("비밀번호가 일치하지 않습니다");
        }

        log.info("{} - 로그인 성공", user.getUserName());

        // 로그인 성공 후 클라이언트에 무엇을 리턴할 것인가?
//        JWT(JSON WEB TOKEN)을 클라이언트에게 발급해줘야 함. -> 토큰 발급 객체를 따로 만들어서 인증 함.
        String token = tokenProvider.createToken(user);

        return new LoginResposeDTO(user, token);


    }

//프리미엄으로 등급업
    public LoginResposeDTO promoteToPremium(TokenUserInfo userInfo)
            throws NoRegisteredArgumentsException, IllegalStateException{
        //예외처리
        User foundUser = userRepository.findById(userInfo.getUserId())
                .orElseThrow(() -> new NoRegisteredArgumentsException("회원조회실패"));

        //일반 회원이 아니면 예외
        if(userInfo.getRole() != Role.COMMON){
            throw new IllegalStateException("일반회원이 아니면 등급을 상승시킬 수 없습니다");
        }
        //등급 변경(setter를 가급적 사용하지 않고!)
        foundUser.changeRole(Role.PREMIUM);
        User saved = userRepository.save(foundUser);

        //토큰을 재발급 받아야 함
        String token = tokenProvider.createToken(saved);

        return new LoginResposeDTO(saved,token);

    }
}




