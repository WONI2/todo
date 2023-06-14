package com.example.todo.todoapi.repository;

import com.example.todo.todoapi.entity.Todo;
import com.example.todo.userapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, String> {

    //특정 회원의 할 일 목록 리턴
    @Query("SELECT t FROM Todo t WHERE t.user= :user")
//    @Query("SELECT * FROM tbl_todo t WHERE user_id=?" Native=true) 로 작성하면 native query 작성 가능

    List<Todo> findAllByUser(@Param("user") User user);

    //회원이 작성한 일정의 개수 리턴
    @Query("SELECT COUNT(*) FROM Todo t WHERE t.user =:user")
    int countByUser(@Param("user")User user);

}
