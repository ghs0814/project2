package com.example.project2.mapper;

import com.example.project2.domain.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    List<Employee> findAll();

    Employee findById(@Param("employeeId") Long employeeId);
}