package com.example.project2.service;

import com.example.project2.domain.Item;
import com.example.project2.dto.ItemRequestDto;
import com.example.project2.dto.ItemResponseDto;
import com.example.project2.exception.BusinessException;
import com.example.project2.mapper.ItemMapper;
import com.example.project2.mapper.RentalMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemMapper itemMapper;
    private final RentalMapper rentalMapper;

    // 1. 전체 조회
    @Transactional(readOnly = true)
    public List<ItemResponseDto> findAll() {

        return itemMapper.findAll()
                .stream()
                .map(ItemResponseDto::from)
                .toList();
    }

    // 2. 상세 조회
    @Transactional(readOnly = true)
    public ItemResponseDto findById(Long itemId) {

        Item item = itemMapper.findById(itemId);

        if (item == null) {
            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "해당 사무용품을 찾을 수 없습니다."
            );
        }

        return ItemResponseDto.from(item);
    }

    // 3. 신규 등록
    @Transactional
    public ItemResponseDto create(ItemRequestDto request) {

        Item item = new Item();

        item.setItemName(request.getItemName());
        item.setTotalQuantity(request.getTotalQuantity());

        // 신규 등록 시 가용 수량 = 전체 수량
        item.setAvailableQuantity(request.getTotalQuantity());

        int insertedRows = itemMapper.insert(item);

        if (insertedRows != 1) {
            throw new IllegalStateException(
                    "사무용품 등록에 실패했습니다."
            );
        }

        return ItemResponseDto.from(item);
    }

    // 4. 수정
    @Transactional
    public ItemResponseDto update(
            Long itemId,
            ItemRequestDto request) {

        // 행 잠금 조회
        Item item = itemMapper.findByIdForUpdate(itemId);

        if (item == null) {
            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "해당 사무용품을 찾을 수 없습니다."
            );
        }

        // 현재 대여 중인 기록 확인
        long activeCount = rentalMapper.countActiveByItemId(itemId);

        if (activeCount > 0) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    "현재 대여 중인 사무용품은 수정할 수 없습니다."
            );
        }

        // 대여 중인 기록이 없을 때만 수정
        item.setItemName(request.getItemName());
        item.setTotalQuantity(request.getTotalQuantity());
        item.setAvailableQuantity(request.getTotalQuantity());

        int updatedRows = itemMapper.update(item);

        if (updatedRows != 1) {
            throw new IllegalStateException(
                    "사무용품 수정에 실패했습니다."
            );
        }

        return ItemResponseDto.from(item);
    }

    // 5. 삭제
    @Transactional
    public void delete(Long itemId) {

        // 행 잠금 조회
        Item item = itemMapper.findByIdForUpdate(itemId);

        if (item == null) {
            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "해당 사무용품을 찾을 수 없습니다."
            );
        }

        // 반납 완료를 포함한 전체 대여 이력 확인
        long rentalCount = rentalMapper.countByItemId(itemId);

        if (rentalCount > 0) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    "대여 이력이 있는 사무용품은 삭제할 수 없습니다."
            );
        }

        int deletedRows = itemMapper.deleteById(itemId);

        if (deletedRows != 1) {
            throw new IllegalStateException(
                    "사무용품 삭제에 실패했습니다."
            );
        }
    }
}