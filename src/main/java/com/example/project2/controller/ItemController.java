package com.example.project2.controller;

import com.example.project2.dto.ItemRequestDto;
import com.example.project2.dto.ItemResponseDto;
import com.example.project2.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    // 1. 전체 조회
    @GetMapping
    public List<ItemResponseDto> findAll() {

        return itemService.findAll();
    }

    // 2. 상세 조회
    @GetMapping("/{itemId}")
    public ItemResponseDto findById(
            @PathVariable("itemId") Long itemId) {

        return itemService.findById(itemId);
    }

    // 3. 등록
    @PostMapping
    public ResponseEntity<ItemResponseDto> create(
            @Valid @RequestBody ItemRequestDto request) {

        ItemResponseDto response = itemService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 4. 수정
    @PutMapping("/{itemId}")
    public ItemResponseDto update(
            @PathVariable("itemId") Long itemId,
            @Valid @RequestBody ItemRequestDto request) {

        return itemService.update(itemId, request);
    }

    // 5. 삭제
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> delete(
            @PathVariable("itemId") Long itemId) {

        itemService.delete(itemId);

        return ResponseEntity.noContent().build();
    }
}