package com.example.todo.userapi.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;


import javax.persistence.*;
import java.time.LocalDateTime;

 @Getter @ToString
@EqualsAndHashCode(of ="id")
@NoArgsConstructor @AllArgsConstructor
@Builder

@Entity
@Table(name="tbl_user")
public class User  {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id; // 계정명이 아니라 식별코드

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String userName;

    @CreationTimestamp
    private LocalDateTime joinDate;

    @Enumerated(EnumType.STRING)
//    @ColumnDefault("'COMMON'") // ENUM은 '' 로 다시 감싸줄 것.
    @Builder.Default
    private Role role = Role.COMMON; // 유저권한


     private String profileImg;

//setter 없이 등급수정메서드를 새로 만들어서 사용하도록 할 것
     public void changeRole(Role role){
         this.role = role;

     }





}
