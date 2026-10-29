package com.example.project2.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Rental {

    private Long rentalId;

    private Long employeeId;

    private Long itemId;

    private Integer quantity;

    //TIMESTAMP WITHOUT TIME ZONE
    private LocalDateTime rentedAt;

    private LocalDateTime returnedAt;
}