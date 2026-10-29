package com.example.project2.dto;

import com.example.project2.domain.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemResponseDto {

    private Long itemId;

    private String itemName;

    private Integer totalQuantity;

    private Integer availableQuantity;

    public static ItemResponseDto from(Item item) {

        return new ItemResponseDto(
                item.getItemId(),
                item.getItemName(),
                item.getTotalQuantity(),
                item.getAvailableQuantity()
        );
    }
}