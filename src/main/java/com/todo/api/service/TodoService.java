package com.todo.api.service;

import com.todo.api.model.CreateTodoRequest;
import com.todo.api.model.Todo;
import com.todo.api.model.UpdateTodoRequest;
import com.todo.api.repository.TodoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TodoService {
    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Transactional(readOnly = true)
    public List<Todo> list() {
        return todoRepository.findAll();
    }

    @Transactional
    public Todo create(CreateTodoRequest request) {
        Todo todo = new Todo(UUID.randomUUID().toString(), request.getTitle().trim(), false, Instant.now());
        return todoRepository.save(todo);
    }

    @Transactional
    public Todo update(String id, UpdateTodoRequest request) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found"));
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
        return todoRepository.save(todo);
    }

    @Transactional
    public void delete(String id) {
        if (!todoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found");
        }
        todoRepository.deleteById(id);
    }
}
