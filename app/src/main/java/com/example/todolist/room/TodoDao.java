package com.example.todolist.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TodoDao {

    @Insert
    void insertTodo(Todoitem item);
    @Delete
    void deleteTodo(Todoitem item);
    @Query("delete FROM TODO")
    void deleteAllTodo();
    @Update
    void editTodo(Todoitem todoitem);
    @Query("select * FROM TODO WHERE id = :id")
    Todoitem getTodo(int id);
    @Query("select * FROM TODO")
    List<Todoitem> getAllTodo();
}
