package com.nje_web.beadando.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestControllerAdvice(assignableTypes = LabdarugoApiController.class)
public class LabdarugoApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiHiba> ervenytelenAdat(MethodArgumentNotValidException exception) {
        List<String> hibak = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        return ResponseEntity.badRequest().body(new ApiHiba("A kérés adatai hibásak.", hibak));
    }

    // Hibás JSON (szintaktikai hiba, rossz dátumformátum, szöveg szám helyett)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiHiba> olvashatatlanKeres(HttpMessageNotReadableException exception) {
        return ResponseEntity.badRequest().body(new ApiHiba("A kérés törzse nem érvényes JSON, vagy hibás formátumú mezőt tartalmaz.", List.of()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiHiba> apiHiba(ResponseStatusException exception) {
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
        return ResponseEntity.status(status).body(new ApiHiba(exception.getReason(), List.of()));
    }
}
