package com.example.project2.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Item {

    //BIGINT
    private Long itemId;

    private String itemName;

    private Integer totalQuantity;

    private Integer availableQuantity;
}