package com.example.todo.todoapi.dto.request;


import lombok.*;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class TodoModifyRequestDTO {

    private String id;
    private boolean done;

}
