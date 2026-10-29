package com.example.project2.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RentalCreateRequestDto {

    @NotNull(message = "직원 ID는 필수입니다.")
    @Min(value = 1, message = "직원 ID는 1 이상이어야 합니다.")
    private Long employeeId;

    @NotNull(message = "사무용품 ID는 필수입니다.")
    @Min(value = 1, message = "사무용품 ID는 1 이상이어야 합니다.")
    private Long itemId;

    @NotNull(message = "대여 수량은 필수입니다.")
    @Min(value = 1, message = "대여 수량은 1 이상이어야 합니다.")
    private Integer quantity;
}