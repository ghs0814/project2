package com.example.project2.mapper;

import com.example.project2.domain.Rental;
import com.example.project2.dto.RentalHistoryResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RentalMapper {

    // 대여 등록
    int insert(Rental rental);

    // 대여 상세 조회
    Rental findById(@Param("rentalId") Long rentalId);

    // 반납 완료 처리
    int markReturned(@Param("rentalId") Long rentalId);

    // 검색 + 페이징
    List<RentalHistoryResponseDto> searchHistoryPage(
            @Param("employeeName") String employeeName,
            @Param("itemName") String itemName,
            @Param("rentalStatus") String rentalStatus,
            @Param("size") int size,
            @Param("offset") long offset
    );

    // 검색 결과 전체 건수
    long countHistory(
            @Param("employeeName") String employeeName,
            @Param("itemName") String itemName,
            @Param("rentalStatus") String rentalStatus
    );

    // 특정 사무용품의 현재 대여 중인 기록 수
    long countActiveByItemId(
            @Param("itemId") Long itemId
    );

    // 특정 사무용품의 전체 대여 이력 수
    long countByItemId(
            @Param("itemId") Long itemId
    );
}