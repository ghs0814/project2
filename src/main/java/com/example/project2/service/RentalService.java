package com.example.project2.service;

import com.example.project2.domain.Item;
import com.example.project2.domain.Rental;
import com.example.project2.dto.RentalCreateRequestDto;
import com.example.project2.dto.RentalPageResponseDto;
import com.example.project2.dto.RentalResponseDto;
import com.example.project2.dto.RentalHistoryResponseDto;
import com.example.project2.exception.BusinessException;
import com.example.project2.mapper.EmployeeMapper;
import com.example.project2.mapper.ItemMapper;
import com.example.project2.mapper.RentalMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalMapper rentalMapper;
    private final EmployeeMapper employeeMapper;
    private final ItemMapper itemMapper;

    // 1. 대여 등록
    @Transactional
    public RentalResponseDto create(RentalCreateRequestDto request) {

        // 요청값 검사
        if (request == null ||
                request.getEmployeeId() == null ||
                request.getItemId() == null ||
                request.getQuantity() == null ||
                request.getEmployeeId() <= 0 ||
                request.getItemId() <= 0 ||
                request.getQuantity() <= 0) {

            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "대여 요청 데이터가 올바르지 않습니다."
            );
        }

        // 직원 존재 여부 확인
        if (employeeMapper.findById(request.getEmployeeId()) == null) {
            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "해당 직원을 찾을 수 없습니다."
            );
        }

        // 사무용품 존재 여부 확인
        Item item = itemMapper.findById(request.getItemId());

        if (item == null) {
            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "해당 사무용품을 찾을 수 없습니다."
            );
        }

        // 재고 차감
        int updatedRows = itemMapper.decreaseAvailableQuantity(
                request.getItemId(),
                request.getQuantity()
        );

        if (updatedRows != 1) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    "대여 가능 재고가 부족하거나 사무용품 상태가 변경되었습니다."
            );
        }

        // 대여 기록 생성
        Rental rental = new Rental();

        rental.setEmployeeId(request.getEmployeeId());
        rental.setItemId(request.getItemId());
        rental.setQuantity(request.getQuantity());

        int insertedRows = rentalMapper.insert(rental);

        if (insertedRows != 1) {
            throw new IllegalStateException(
                    "대여 기록 저장에 실패했습니다."
            );
        }

        // 저장된 기록 조회
        Rental savedRental = rentalMapper.findById(rental.getRentalId());

        if (savedRental == null) {
            throw new IllegalStateException(
                    "저장된 대여 기록을 찾을 수 없습니다."
            );
        }

        return RentalResponseDto.from(savedRental);
    }

    // 2. 대여 상세 조회
    @Transactional(readOnly = true)
    public RentalResponseDto findById(Long rentalId) {

        Rental rental = rentalMapper.findById(rentalId);

        if (rental == null) {
            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "해당 대여 기록을 찾을 수 없습니다."
            );
        }

        return RentalResponseDto.from(rental);
    }

    // 3. 반납 처리
    @Transactional
    public RentalResponseDto returnRental(Long rentalId) {

        // 대여 기록 조회
        Rental rental = rentalMapper.findById(rentalId);

        if (rental == null) {
            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "해당 대여 기록을 찾을 수 없습니다."
            );
        }

        // 중복 반납 방지
        int returnedRows = rentalMapper.markReturned(rentalId);

        if (returnedRows != 1) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    "이미 반납 처리된 대여 기록입니다."
            );
        }

        // 재고 복구
        int restoredRows = itemMapper.increaseAvailableQuantity(
                rental.getItemId(),
                rental.getQuantity()
        );

        if (restoredRows != 1) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    "재고 복구에 실패했습니다. 사무용품 수량을 확인하세요."
            );
        }

        // 반납 완료 기록 재조회
        Rental returnedRental = rentalMapper.findById(rentalId);

        if (returnedRental == null) {
            throw new IllegalStateException(
                    "반납 처리된 기록을 찾을 수 없습니다."
            );
        }

        return RentalResponseDto.from(returnedRental);
    }

    // 4. 검색 + 페이징
    @Transactional(readOnly = true)
    public RentalPageResponseDto searchHistoryPage(
            String employeeName,
            String itemName,
            String rentalStatus,
            int page,
            int size) {

        // 페이지 값 검증
        if (page < 1 || size < 1 || size > 100) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "page는 1 이상, size는 1~100 사이여야 합니다."
            );
        }

        // 검색어 정리
        String normalizedEmployeeName = normalizeKeyword(employeeName);
        String normalizedItemName = normalizeKeyword(itemName);
        String normalizedStatus = normalizeKeyword(rentalStatus);

        // 대여 상태 검증
        if (normalizedStatus != null) {

            normalizedStatus = normalizedStatus.toUpperCase(Locale.ROOT);

            if (!normalizedStatus.equals("RENTED") &&
                    !normalizedStatus.equals("RETURNED")) {

                throw new BusinessException(
                        HttpStatus.BAD_REQUEST,
                        "대여 상태는 RENTED 또는 RETURNED만 가능합니다."
                );
            }
        }

        // OFFSET 계산
        long offset = ((long) page - 1) * size;

        // 전체 검색 건수 조회
        long totalElements = rentalMapper.countHistory(
                normalizedEmployeeName,
                normalizedItemName,
                normalizedStatus
        );

        // 페이지 목록 조회
        List<RentalHistoryResponseDto> content =
                rentalMapper.searchHistoryPage(
                        normalizedEmployeeName,
                        normalizedItemName,
                        normalizedStatus,
                        size,
                        offset
                );

        // 전체 페이지 수 계산
        long totalPages = totalElements / size;

        if (totalElements % size != 0) {
            totalPages++;
        }

        // 이전/다음 페이지 존재 여부
        boolean hasPrevious = page > 1 && totalPages > 0;
        boolean hasNext = page < totalPages;

        return new RentalPageResponseDto(
                content,
                page,
                size,
                totalElements,
                totalPages,
                hasPrevious,
                hasNext
        );
    }

    // 검색어 공백 처리
    private String normalizeKeyword(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        return keyword.trim();
    }
}