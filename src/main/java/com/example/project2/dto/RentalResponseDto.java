package com.example.project2.dto;

import com.example.project2.domain.Rental;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RentalResponseDto {

    private Long rentalId;

    private Long employeeId;

    private Long itemId;

    private Integer quantity;

    private LocalDateTime rentedAt;

    private LocalDateTime returnedAt;

    public static RentalResponseDto from(Rental rental) {

        return new RentalResponseDto(
                rental.getRentalId(),
                rental.getEmployeeId(),
                rental.getItemId(),
                rental.getQuantity(),
                rental.getRentedAt(),
                rental.getReturnedAt()
        );
    }
}