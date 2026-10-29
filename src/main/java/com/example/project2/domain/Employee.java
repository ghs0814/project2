package com.example.project2.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Employee {

    private Long employeeId;

    private String employeeName;

    private String department;
}