package com.example.project2.service;

import com.example.project2.domain.Item;
import com.example.project2.dto.ItemRequestDto;
import com.example.project2.dto.ItemResponseDto;
import com.example.project2.exception.BusinessException;
import com.example.project2.mapper.ItemMapper;
import com.example.project2.mapper.RentalMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private RentalMapper rentalMapper;

    @InjectMocks
    private ItemService itemService;

    @Test
    void findAll_shouldReturnResponseDtos() {

        // given
        Item item = createItem(1L, "노트북", 5, 5);

        when(itemMapper.findAll())
                .thenReturn(List.of(item));

        // when
        List<ItemResponseDto> result = itemService.findAll();

        // then
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getItemId());
        assertEquals("노트북", result.get(0).getItemName());
        assertEquals(5, result.get(0).getAvailableQuantity());

        verify(itemMapper).findAll();
    }

    @Test
    void findById_shouldThrowWhenItemNotFound() {

        // given
        when(itemMapper.findById(999L))
                .thenReturn(null);

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> itemService.findById(999L)
        );

        // then
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(itemMapper).findById(999L);
    }

    @Test
    void create_shouldSetAvailableQuantityToTotal() {

        // given
        ItemRequestDto request = createRequest("마우스", 8);

        when(itemMapper.insert(any(Item.class)))
                .thenAnswer(invocation -> {

                    Item item = invocation.getArgument(0);

                    item.setItemId(10L);

                    return 1;
                });

        // when
        ItemResponseDto result = itemService.create(request);

        // then
        assertEquals(10L, result.getItemId());
        assertEquals("마우스", result.getItemName());
        assertEquals(8, result.getTotalQuantity());
        assertEquals(8, result.getAvailableQuantity());

        ArgumentCaptor<Item> captor =
                ArgumentCaptor.forClass(Item.class);

        verify(itemMapper).insert(captor.capture());

        Item savedItem = captor.getValue();

        assertEquals(8, savedItem.getTotalQuantity());
        assertEquals(8, savedItem.getAvailableQuantity());
    }

    @Test
    void update_shouldRejectWhenItemIsRented() {

        // given
        Item item = createItem(1L, "노트북", 5, 3);
        ItemRequestDto request = createRequest("새 노트북", 10);

        when(itemMapper.findByIdForUpdate(1L))
                .thenReturn(item);

        when(rentalMapper.countActiveByItemId(1L))
                .thenReturn(1L);

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> itemService.update(1L, request)
        );

        // then
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());

        verify(itemMapper, never()).update(any(Item.class));
    }

    @Test
    void update_shouldResetStockWhenNoActiveRentals() {

        // given
        Item item = createItem(1L, "노트북", 5, 5);
        ItemRequestDto request = createRequest("업무용 노트북", 10);

        when(itemMapper.findByIdForUpdate(1L))
                .thenReturn(item);

        when(rentalMapper.countActiveByItemId(1L))
                .thenReturn(0L);

        when(itemMapper.update(any(Item.class)))
                .thenReturn(1);

        // when
        ItemResponseDto result = itemService.update(1L, request);

        // then
        assertEquals("업무용 노트북", result.getItemName());
        assertEquals(10, result.getTotalQuantity());
        assertEquals(10, result.getAvailableQuantity());

        verify(itemMapper).update(item);
    }

    @Test
    void delete_shouldRejectWhenRentalHistoryExists() {

        // given
        Item item = createItem(1L, "노트북", 5, 5);

        when(itemMapper.findByIdForUpdate(1L))
                .thenReturn(item);

        when(rentalMapper.countByItemId(1L))
                .thenReturn(3L);

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> itemService.delete(1L)
        );

        // then
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());

        verify(itemMapper, never()).deleteById(1L);
    }

    @Test
    void delete_shouldDeleteItemWithoutRentalHistory() {

        // given
        Item item = createItem(1L, "테스트 마우스", 5, 5);

        when(itemMapper.findByIdForUpdate(1L))
                .thenReturn(item);

        when(rentalMapper.countByItemId(1L))
                .thenReturn(0L);

        when(itemMapper.deleteById(1L))
                .thenReturn(1);

        // when
        itemService.delete(1L);

        // then
        verify(itemMapper).deleteById(1L);
    }

    // 테스트 데이터 생성용 메서드
    private Item createItem(
            Long id,
            String name,
            int total,
            int available) {

        Item item = new Item();

        item.setItemId(id);
        item.setItemName(name);
        item.setTotalQuantity(total);
        item.setAvailableQuantity(available);

        return item;
    }

    private ItemRequestDto createRequest(
            String name,
            int total) {

        ItemRequestDto request = new ItemRequestDto();

        request.setItemName(name);
        request.setTotalQuantity(total);

        return request;
    }
}