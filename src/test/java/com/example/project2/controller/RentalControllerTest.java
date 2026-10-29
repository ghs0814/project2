package com.example.project2.controller;

import com.example.project2.dto.RentalCreateRequestDto;
import com.example.project2.dto.RentalHistoryResponseDto;
import com.example.project2.dto.RentalPageResponseDto;
import com.example.project2.dto.RentalResponseDto;
import com.example.project2.exception.BusinessException;
import com.example.project2.exception.GlobalExceptionHandler;
import com.example.project2.service.RentalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RentalController.class)
@Import(GlobalExceptionHandler.class)
class RentalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RentalService rentalService;

    @Test
    void create_shouldReturnCreated() throws Exception {

        // given
        RentalResponseDto response = createRentalResponse(
                10L,
                null
        );

        when(rentalService.create(any(RentalCreateRequestDto.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "employeeId": 1,
                                    "itemId": 2,
                                    "quantity": 2
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rentalId").value(10))
                .andExpect(jsonPath("$.employeeId").value(1))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.returnedAt").value((Object) null));
    }

    @Test
    void create_shouldRejectInvalidQuantity() throws Exception {

        // given
        String json = """
                {
                    "employeeId": 1,
                    "itemId": 2,
                    "quantity": -1
                }
                """;

        // when & then
        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(rentalService);
    }

    @Test
    void returnRental_shouldReturnUpdatedRental() throws Exception {

        // given
        LocalDateTime returnedAt =
                LocalDateTime.of(2026, 10, 28, 10, 0);

        RentalResponseDto response =
                createRentalResponse(10L, returnedAt);

        when(rentalService.returnRental(10L))
                .thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/rentals/10/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rentalId").value(10))
                .andExpect(jsonPath("$.returnedAt").isNotEmpty());

        verify(rentalService).returnRental(10L);
    }

    @Test
    void returnRental_shouldReturnConflictWhenAlreadyReturned()
            throws Exception {

        // given
        when(rentalService.returnRental(10L))
                .thenThrow(new BusinessException(
                        HttpStatus.CONFLICT,
                        "이미 반납 처리된 대여 기록입니다."
                ));

        // when & then
        mockMvc.perform(post("/api/rentals/10/return"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value("이미 반납 처리된 대여 기록입니다."));
    }

    @Test
    void searchHistoryPage_shouldReturnPageResponse()
            throws Exception {

        // given
        RentalHistoryResponseDto history =
                new RentalHistoryResponseDto();

        history.setRentalId(10L);
        history.setEmployeeName("김철수");
        history.setItemName("노트북");
        history.setRentalStatus("RENTED");

        RentalPageResponseDto response =
                new RentalPageResponseDto(
                        List.of(history),
                        1,
                        2,
                        3,
                        2,
                        false,
                        true
                );

        when(rentalService.searchHistoryPage(
                "김", null, "RENTED", 1, 2
        )).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/rentals")
                        .param("employeeName", "김")
                        .param("rentalStatus", "RENTED")
                        .param("page", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].employeeName")
                        .value("김철수"))
                .andExpect(jsonPath("$.content[0].itemName")
                        .value("노트북"))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.hasNext").value(true));
    }

    @Test
    void searchHistoryPage_shouldUseDefaultPagination()
            throws Exception {

        // given
        RentalPageResponseDto response =
                new RentalPageResponseDto(
                        List.of(),
                        1,
                        10,
                        0,
                        0,
                        false,
                        false
                );

        when(rentalService.searchHistoryPage(
                null, null, null, 1, 10
        )).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(0));

        verify(rentalService).searchHistoryPage(
                null, null, null, 1, 10
        );
    }

    // 테스트용 응답 DTO 생성
    private RentalResponseDto createRentalResponse(
            Long rentalId,
            LocalDateTime returnedAt) {

        return new RentalResponseDto(
                rentalId,
                1L,
                2L,
                2,
                LocalDateTime.of(2026, 10, 28, 9, 0),
                returnedAt
        );
    }
}