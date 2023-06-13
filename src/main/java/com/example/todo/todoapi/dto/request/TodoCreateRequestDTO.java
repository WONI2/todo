package com.example.todo.todoapi.dto.request;

import com.example.todo.todoapi.entity.Todo;
import com.example.todo.userapi.entity.User;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter @Getter
@ToString @EqualsAndHashCode
@AllArgsConstructor @NoArgsConstructor
@Builder

public class TodoCreateRequestDTO {
    @NotBlank
    @Size(min=2, max = 10)
    private String title;


    public Todo toEntity() {
        return Todo.builder()
                .title(this.title)
                .build();
    }

// 나중에 문제가 생길 수 있으니 오버로딩으로 처리.
    public Todo toEntity(User user) {
        return Todo.builder()
                .title(this.title)
                .user(user)
                .build();
    }



}
