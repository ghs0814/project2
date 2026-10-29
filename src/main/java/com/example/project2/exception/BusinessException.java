package com.example.project2.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus status;

    public BusinessException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
//throw new BusinessException(
//        HttpStatus.NOT_FOUND,
//        "해당 사무용품을 찾을 수 없습니다."
//);