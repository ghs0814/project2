package com.example.project2.exception;

import com.example.project2.dto.ErrorResponseDto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 업무 예외
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDto> handleBusiness(
            BusinessException e) {

        ErrorResponseDto response = new ErrorResponseDto(
                e.getStatus().value(), // code
                e.getMessage()
        );

        return ResponseEntity
                .status(e.getStatus()) // status
                .body(response);
    }

    // 2. @Valid 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(
            MethodArgumentNotValidException e) {

        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error ->
                        error.getField() + ": " + error.getDefaultMessage()
                )
                .orElse("요청 데이터가 올바르지 않습니다.");

        ErrorResponseDto response = new ErrorResponseDto(
                400,
                message
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    // 3. JSON 형식 또는 타입 오류
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleJson(
            HttpMessageNotReadableException e) {

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponseDto(
                        400,
                        "JSON 요청 형식이 올바르지 않습니다."
                ));
    }

    // 4. URL 파라미터 타입 오류
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleTypeMismatch(
            MethodArgumentTypeMismatchException e) {

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponseDto(
                        400,
                        "요청 파라미터의 타입이 올바르지 않습니다."
                ));
    }

    // 5. 데이터베이스 무결성 제약조건 위반
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrity(
            DataIntegrityViolationException e) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDto(
                        409,
                        "데이터베이스 제약조건을 위반했습니다."
                ));
    }
}