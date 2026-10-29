package com.example.project2;

import com.example.project2.domain.Item;
import com.example.project2.mapper.ItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MapperCheckRunner implements CommandLineRunner {

    private final ItemMapper itemMapper;

    @Override
    public void run(String... args) {

        // 1. 전체 조회
        List<Item> items = itemMapper.findAll();

        System.out.println("========== ItemMapper 테스트 ==========");

        System.out.println("전체 사무용품 수: " + items.size());

        for (Item item : items) {
            System.out.println(
                    item.getItemId() + " / " +
                            item.getItemName() + " / " +
                            item.getTotalQuantity() + " / " +
                            item.getAvailableQuantity()
            );
        }

        // 2. 상세 조회
        if (!items.isEmpty()) {

            Long itemId = items.get(0).getItemId();

            Item item = itemMapper.findById(itemId);

            System.out.println("상세 조회 ID: " + item.getItemId());
            System.out.println("상세 조회 이름: " + item.getItemName());
        }

        System.out.println("=======================================");
    }
}