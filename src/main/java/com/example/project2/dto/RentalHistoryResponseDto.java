package com.example.project2.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter //조회 결과를 바로 매핑
@NoArgsConstructor
public class RentalHistoryResponseDto {

    private Long rentalId;

    private Long employeeId;

    private String employeeName;

    private String department;

    private Long itemId;

    private String itemName;

    private Integer quantity;

    private LocalDateTime rentedAt;

    private LocalDateTime returnedAt;

    private String rentalStatus;
}