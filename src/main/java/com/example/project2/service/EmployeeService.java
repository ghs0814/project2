package com.example.project2.service;

import com.example.project2.domain.Employee;
import com.example.project2.dto.EmployeeResponseDto;
import com.example.project2.exception.BusinessException;
import com.example.project2.mapper.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeMapper employeeMapper;

    // 직원 전체 조회
    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> findAll() {

        return employeeMapper.findAll()
                .stream()
                .map(EmployeeResponseDto::from)
                .toList();
    }

    // 직원 상세 조회
    @Transactional(readOnly = true)
    public EmployeeResponseDto findById(Long employeeId) {

        Employee employee = employeeMapper.findById(employeeId);

        if (employee == null) {
            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "해당 직원을 찾을 수 없습니다."
            );
        }

        return EmployeeResponseDto.from(employee);
    }
}