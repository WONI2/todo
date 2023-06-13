package com.example.todo.config;

import com.example.todo.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

//@Configuration
@EnableWebSecurity //Configuration 포함 됨
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors()
                .and()
                .csrf().disable()
                .httpBasic().disable()
                //    JWT FILTER를 언제 실행할지 설정
                //세션인증을 사용하지 않고 토큰을 이용하겠다.
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                //어떤 요청에서 인증을 안 할 것인지 설정, 언제할 것인지 설정
                .authorizeRequests().antMatchers("/","/api/auth/**").permitAll() //백엔드의 이 경로는 모두 허용
                .anyRequest().authenticated() //위의 경로 제외하고는 모두 인증받기
        ;
        //토큰인증 필터 연결 corsfilter와 logoutfilter 사이에
        http.addFilterAfter(
          jwtAuthFilter,
                CorsFilter.class //spring으로 import 할 것

        );


        return http.build();
    }



}