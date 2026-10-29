package com.example.project2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RentalPageResponseDto {

    private List<RentalHistoryResponseDto> content;

    private int page;

    private int size;

    //count
    private long totalElements;

    private long totalPages;

    private boolean hasPrevious;

    private boolean hasNext;
}