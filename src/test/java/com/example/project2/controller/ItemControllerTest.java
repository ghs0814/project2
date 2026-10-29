package com.example.project2.controller;

import com.example.project2.dto.ItemRequestDto;
import com.example.project2.dto.ItemResponseDto;
import com.example.project2.exception.BusinessException;
import com.example.project2.exception.GlobalExceptionHandler;
import com.example.project2.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@Import(GlobalExceptionHandler.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemService itemService;

    @Test
    void findAll_shouldReturnItems() throws Exception {

        // given
        ItemResponseDto response =
                new ItemResponseDto(1L, "노트북", 5, 3);

        when(itemService.findAll())
                .thenReturn(List.of(response));

        // when & then
        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].itemId").value(1))
                .andExpect(jsonPath("$[0].itemName").value("노트북"))
                .andExpect(jsonPath("$[0].availableQuantity").value(3));
    }

    @Test
    void create_shouldReturnCreated() throws Exception {

        // given
        ItemResponseDto response =
                new ItemResponseDto(10L, "마우스", 5, 5);

        when(itemService.create(any(ItemRequestDto.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "itemName": "마우스",
                                    "totalQuantity": 5
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemId").value(10))
                .andExpect(jsonPath("$.itemName").value("마우스"))
                .andExpect(jsonPath("$.availableQuantity").value(5));

        verify(itemService).create(any(ItemRequestDto.class));
    }

    @Test
    void create_shouldRejectBlankItemName() throws Exception {

        // given
        String json = """
                {
                    "itemName": "",
                    "totalQuantity": 5
                }
                """;

        // when & then
        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("itemName: 사무용품 이름은 필수입니다."));

        verifyNoInteractions(itemService);
    }

    @Test
    void update_shouldRejectNegativeQuantity() throws Exception {

        // given
        String json = """
                {
                    "itemName": "노트북",
                    "totalQuantity": -1
                }
                """;

        // when & then
        mockMvc.perform(put("/api/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(itemService);
    }

    @Test
    void findById_shouldReturnNotFound() throws Exception {

        // given
        when(itemService.findById(999L))
                .thenThrow(new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "해당 사무용품을 찾을 수 없습니다."
                ));

        // when & then
        mockMvc.perform(get("/api/items/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("해당 사무용품을 찾을 수 없습니다."));
    }

    @Test
    void update_shouldReturnUpdatedItem() throws Exception {

        // given
        ItemResponseDto response =
                new ItemResponseDto(1L, "업무용 노트북", 10, 10);

        when(itemService.update(
                anyLong(),
                any(ItemRequestDto.class)
        )).thenReturn(response);

        // when & then
        mockMvc.perform(put("/api/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "itemName": "업무용 노트북",
                                    "totalQuantity": 10
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemName").value("업무용 노트북"))
                .andExpect(jsonPath("$.totalQuantity").value(10))
                .andExpect(jsonPath("$.availableQuantity").value(10));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {

        // given
        Long itemId = 1L;

        // when & then
        mockMvc.perform(delete("/api/items/{itemId}", itemId))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(itemService).delete(itemId);
    }
}