package com.example.project2;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SetupCheckRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final SqlSessionFactory sqlSessionFactory;

    @Override
    public void run(String... args) {

        String database = jdbcTemplate.queryForObject(
                "SELECT current_database()",
                String.class
        );

        String username = jdbcTemplate.queryForObject(
                "SELECT current_user",
                String.class
        );

        Integer itemCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM items",
                Integer.class
        );

        System.out.println("========== Project2 연결 확인 ==========");
        System.out.println("Database: " + database);
        System.out.println("Username: " + username);
        System.out.println("Items count: " + itemCount);
        System.out.println("MyBatis: " + sqlSessionFactory.getClass().getSimpleName());
        System.out.println("=======================================");
    }
}