package com.todo.api.service;

import com.todo.api.model.CreateTodoRequest;
import com.todo.api.model.Todo;
import com.todo.api.model.UpdateTodoRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TodoService {
    private final Map<String, Todo> todos = new ConcurrentHashMap<>();

    public List<Todo> list() {
        return new ArrayList<>(todos.values());
    }

    public Todo create(CreateTodoRequest request) {
        String id = UUID.randomUUID().toString();
        Todo todo = new Todo(id, request.getTitle().trim(), false, Instant.now());
        todos.put(id, todo);
        return todo;
    }

    public Todo update(String id, UpdateTodoRequest request) {
        Todo todo = todos.get(id);
        if (todo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found");
        }
        if (request.getTitle() != null) {
            String title = request.getTitle().trim();
            if (title.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title must not be blank");
            }
            todo.setTitle(title);
        }
        if (request.getCompleted() != null) {
            todo.setCompleted(request.getCompleted());
        }
        return todo;
    }

    public void delete(String id) {
        if (todos.remove(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found");
        }
    }
}
