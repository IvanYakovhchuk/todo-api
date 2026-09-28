package com.todo.api.model;

import jakarta.validation.constraints.NotBlank;

public class CreateTodoRequest {
    @NotBlank
    private String title;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}
