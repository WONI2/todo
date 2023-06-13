package com.example.todo.auth;


import com.example.todo.userapi.entity.Role;
import com.example.todo.userapi.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
//역할: 토큰을 발급하고 서명위조를 검사하는 객체 -> service가 의존할 수 있게 bean 등록
public class TokenProvider {
    //토큰 발급을 위한 서명이 필요 -> 여기에서 사용할 값 필요(512비트 이상의 랜덤 문자열로 만들 것)
    @Value(("${jwt.secret}"))
    private String SECRET_KEY;

//    토큰 만료 시간 생성




//    토큰 생성 메서드

    /**
     * Json Web Token을 생성하는 메서트
     * @param userEntity -토큰의 내용에 포함될 유저정보
     * @return - 생성된 JSON을 암호화한 토큰 값
     *
     * {
     *     "iss" : "안녕",
     *     "exp" : "2023-08-12",
     *     "iat" : "2023-06-12",
     *     ...
     *     ==서명
     * }  -> 커스텀도 가능
     *
     */

//    추가 클레임 정의



    public String createToken(User userEntity) {
        Date expiry = Date.from(
                Instant.now().plus(1, ChronoUnit.DAYS) //하루로 설정
        );

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", userEntity.getEmail());
        claims.put("role", userEntity.getRole().toString());



        return Jwts.builder()
                //token header에 들어갈 서명 SECRET_KEY 암호화 해서 넣기
                .signWith(
                        Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                        , SignatureAlgorithm.HS512
                )
                //token body에 payload에 들어갈 서명. 자주 쓰는 클레임은 setter로 저장되어 있음
                .setClaims(claims)
                //map으로 추가하고 싶은 내용을 작성해서 받을 것 . 먼저 셋팅해줘야 나중에 등록한 내용이 사라지지 않음
                .setIssuer("발급자") //iss: 발급자 정보
                .setIssuedAt(new Date()) // iat: 발급시간\
                .setExpiration(expiry) //exp: 만료 시간
                .setSubject(userEntity.getId()) //sub: 토큰을 식별할 수 있는 주요데이터 , user를 구분할 수 있는 id
                .compact();
    }

    /***
     * 클라이언트가 전송한 토큰을 디코딩하여 토큰의 위조여부 확인
     * 토큰을 json으로 파싱해서 클레임(토큰정보)를 리턴
     * @param token
     * @return - 토큰 안에 있는 인증된 유저정보를 반환
     */
    public TokenUserInfo validateAndGetTokenUserInfo(String token) {
        Claims claims = Jwts.parserBuilder()
//                토큰발급자의 발급 당시의 서명을 넣어줌
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .build() //서명 위조 검사. 위조된 경우 예외 발생. 위조x인 경우 페이로드(안의 클레임) 리턴
                .parseClaimsJws(token)
                .getBody();//토큰 안의 body 가져오기

        log.info("claims : {} ", claims);

        return TokenUserInfo.builder()
                .userId(claims.getSubject())
                .email(claims.get("email",String.class)) //map이 string object로 들어갔기 때문
                .role(Role.valueOf(claims.get("role",String.class)))
                .build();
    }


}
