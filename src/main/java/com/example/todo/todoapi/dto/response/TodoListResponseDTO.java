package com.example.todo.todoapi.dto.response;

import lombok.*;

import java.util.List;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class TodoListResponseDTO {

    private String error; //에러발생 시 에러메세지 담을 필드
    private List<TodoDetailResponseDTO> todos;
}
