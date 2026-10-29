package com.example.project2.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ItemRequestDto {

    @NotBlank(message = "사무용품 이름은 필수입니다.")
    @Size(max = 100, message = "사무용품 이름은 100자 이하로 입력하세요.")
    private String itemName;

    @NotNull(message = "전체 수량은 필수입니다.")
    @Min(value = 0, message = "전체 수량은 0 이상이어야 합니다.")
    private Integer totalQuantity;
}