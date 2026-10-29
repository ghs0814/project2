package com.example.project2.mapper;

import com.example.project2.domain.Item;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ItemMapper {

    List<Item> findAll();

    Item findById(@Param("itemId") Long itemId);

    // 수정 및 삭제를 위한 행 잠금 조회
    Item findByIdForUpdate(@Param("itemId") Long itemId);

    int insert(Item item);

    int update(Item item);

    int deleteById(@Param("itemId") Long itemId);

    // 대여 시 재고 차감
    int decreaseAvailableQuantity(
            @Param("itemId") Long itemId,
            @Param("quantity") Integer quantity
    );

    // 반납 시 재고 복구
    int increaseAvailableQuantity(
            @Param("itemId") Long itemId,
            @Param("quantity") Integer quantity
    );
}