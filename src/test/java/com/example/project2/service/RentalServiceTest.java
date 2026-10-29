package com.example.project2.service;

import com.example.project2.domain.Employee;
import com.example.project2.domain.Item;
import com.example.project2.domain.Rental;
import com.example.project2.dto.RentalCreateRequestDto;
import com.example.project2.dto.RentalHistoryResponseDto;
import com.example.project2.dto.RentalPageResponseDto;
import com.example.project2.dto.RentalResponseDto;
import com.example.project2.exception.BusinessException;
import com.example.project2.mapper.EmployeeMapper;
import com.example.project2.mapper.ItemMapper;
import com.example.project2.mapper.RentalMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    private RentalMapper rentalMapper;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private RentalService rentalService;

    @Test
    void create_shouldDecreaseStockAndSaveRental() {

        // given
        RentalCreateRequestDto request = createRequest(1L, 2L, 2);

        when(employeeMapper.findById(1L))
                .thenReturn(new Employee());

        when(itemMapper.findById(2L))
                .thenReturn(new Item());

        when(itemMapper.decreaseAvailableQuantity(2L, 2))
                .thenReturn(1);

        when(rentalMapper.insert(any(Rental.class)))
                .thenAnswer(invocation -> {

                    Rental rental = invocation.getArgument(0);

                    rental.setRentalId(100L);

                    return 1;
                });

        Rental savedRental = createRental(100L, 1L, 2L, 2);

        when(rentalMapper.findById(100L))
                .thenReturn(savedRental);

        // when
        RentalResponseDto result = rentalService.create(request);

        // then
        assertEquals(100L, result.getRentalId());
        assertEquals(1L, result.getEmployeeId());
        assertEquals(2L, result.getItemId());
        assertEquals(2, result.getQuantity());
        assertNull(result.getReturnedAt());

        verify(itemMapper).decreaseAvailableQuantity(2L, 2);
        verify(rentalMapper).insert(any(Rental.class));
    }

    @Test
    void create_shouldRejectWhenStockIsInsufficient() {

        // given
        RentalCreateRequestDto request = createRequest(1L, 2L, 100);

        when(employeeMapper.findById(1L))
                .thenReturn(new Employee());

        when(itemMapper.findById(2L))
                .thenReturn(new Item());

        when(itemMapper.decreaseAvailableQuantity(2L, 100))
                .thenReturn(0);

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> rentalService.create(request)
        );

        // then
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());

        verify(rentalMapper, never()).insert(any(Rental.class));
    }

    @Test
    void create_shouldRejectWhenEmployeeNotFound() {

        // given
        RentalCreateRequestDto request = createRequest(999L, 2L, 1);

        when(employeeMapper.findById(999L))
                .thenReturn(null);

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> rentalService.create(request)
        );

        // then
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verifyNoInteractions(itemMapper, rentalMapper);
    }

    @Test
    void create_shouldRejectInvalidQuantity() {

        // given
        RentalCreateRequestDto request = createRequest(1L, 2L, 0);

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> rentalService.create(request)
        );

        // then
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verifyNoInteractions(employeeMapper, itemMapper, rentalMapper);
    }

    @Test
    void returnRental_shouldRestoreStock() {

        // given
        Rental before = createRental(10L, 1L, 2L, 2);

        Rental after = createRental(10L, 1L, 2L, 2);

        after.setReturnedAt(
                LocalDateTime.of(2026, 10, 28, 10, 0)
        );

        when(rentalMapper.findById(10L))
                .thenReturn(before, after);

        when(rentalMapper.markReturned(10L))
                .thenReturn(1);

        when(itemMapper.increaseAvailableQuantity(2L, 2))
                .thenReturn(1);

        // when
        RentalResponseDto result = rentalService.returnRental(10L);

        // then
        assertEquals(10L, result.getRentalId());
        assertNotNull(result.getReturnedAt());

        verify(rentalMapper).markReturned(10L);
        verify(itemMapper).increaseAvailableQuantity(2L, 2);
    }

    @Test
    void returnRental_shouldRejectDuplicateReturn() {

        // given
        Rental rental = createRental(10L, 1L, 2L, 2);

        rental.setReturnedAt(
                LocalDateTime.of(2026, 10, 28, 9, 0)
        );

        when(rentalMapper.findById(10L))
                .thenReturn(rental);

        when(rentalMapper.markReturned(10L))
                .thenReturn(0);

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> rentalService.returnRental(10L)
        );

        // then
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());

        verify(itemMapper, never())
                .increaseAvailableQuantity(2L, 2);
    }

    @Test
    void returnRental_shouldRejectWhenStockRestoreFails() {

        // given
        Rental rental = createRental(10L, 1L, 2L, 2);

        when(rentalMapper.findById(10L))
                .thenReturn(rental);

        when(rentalMapper.markReturned(10L))
                .thenReturn(1);

        when(itemMapper.increaseAvailableQuantity(2L, 2))
                .thenReturn(0);

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> rentalService.returnRental(10L)
        );

        // then
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());

        verify(rentalMapper).markReturned(10L);
        verify(itemMapper).increaseAvailableQuantity(2L, 2);
    }

    @Test
    void searchHistoryPage_shouldCalculatePageMetadata() {

        // given
        when(rentalMapper.countHistory("김", null, "RENTED"))
                .thenReturn(25L);

        RentalHistoryResponseDto history =
                new RentalHistoryResponseDto();

        history.setRentalId(10L);

        when(rentalMapper.searchHistoryPage(
                "김", null, "RENTED", 10, 10L
        )).thenReturn(List.of(history));

        // when
        RentalPageResponseDto result = rentalService.searchHistoryPage(
                " 김 ",
                null,
                "rented",
                2,
                10
        );

        // then
        assertEquals(2, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(25L, result.getTotalElements());
        assertEquals(3L, result.getTotalPages());

        assertTrue(result.isHasPrevious());
        assertTrue(result.isHasNext());

        assertEquals(1, result.getContent().size());

        verify(rentalMapper).searchHistoryPage(
                "김", null, "RENTED", 10, 10L
        );
    }

    @Test
    void searchHistoryPage_shouldRejectInvalidPage() {

        // given
        int page = 0;
        int size = 10;

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> rentalService.searchHistoryPage(
                        null, null, null, page, size
                )
        );

        // then
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verifyNoInteractions(rentalMapper);
    }

    // 테스트 데이터 생성용 메서드
    private RentalCreateRequestDto createRequest(
            Long employeeId,
            Long itemId,
            int quantity) {

        RentalCreateRequestDto request =
                new RentalCreateRequestDto();

        request.setEmployeeId(employeeId);
        request.setItemId(itemId);
        request.setQuantity(quantity);

        return request;
    }

    private Rental createRental(
            Long rentalId,
            Long employeeId,
            Long itemId,
            int quantity) {

        Rental rental = new Rental();

        rental.setRentalId(rentalId);
        rental.setEmployeeId(employeeId);
        rental.setItemId(itemId);
        rental.setQuantity(quantity);

        rental.setRentedAt(
                LocalDateTime.of(2026, 10, 28, 9, 0)
        );

        return rental;
    }
}