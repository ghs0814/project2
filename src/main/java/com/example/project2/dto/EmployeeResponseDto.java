package com.example.project2.dto;

import com.example.project2.domain.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmployeeResponseDto {

    private Long employeeId;

    private String employeeName;

    private String department;

    public static EmployeeResponseDto from(Employee employee) {

        return new EmployeeResponseDto(
                employee.getEmployeeId(),
                employee.getEmployeeName(),
                employee.getDepartment()
        );
    }
}