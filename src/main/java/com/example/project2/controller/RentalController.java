package com.example.project2.controller;

import com.example.project2.dto.RentalCreateRequestDto;
import com.example.project2.dto.RentalPageResponseDto;
import com.example.project2.dto.RentalResponseDto;
import com.example.project2.service.RentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService rentalService;

    // 1. 대여 등록
    @PostMapping
    public ResponseEntity<RentalResponseDto> create(
            @Valid @RequestBody RentalCreateRequestDto request) {

        RentalResponseDto response = rentalService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 2. 검색 + 페이징
    @GetMapping
    public RentalPageResponseDto searchHistoryPage(
            @RequestParam(name = "employeeName", required = false)
            String employeeName,

            @RequestParam(name = "itemName", required = false)
            String itemName,

            @RequestParam(name = "rentalStatus", required = false)
            String rentalStatus,

            @RequestParam(name = "page", defaultValue = "1")
            int page,

            @RequestParam(name = "size", defaultValue = "10")
            int size) {

        return rentalService.searchHistoryPage(
                employeeName,
                itemName,
                rentalStatus,
                page,
                size
        );
    }

    // 3. 대여 상세 조회
    @GetMapping("/{rentalId}")
    public RentalResponseDto findById(
            @PathVariable("rentalId") Long rentalId) {

        return rentalService.findById(rentalId);
    }

    // 4. 반납 처리
    @PostMapping("/{rentalId}/return")
    public RentalResponseDto returnRental(
            @PathVariable("rentalId") Long rentalId) {

        return rentalService.returnRental(rentalId);
    }
}