package com.software.testing;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"com.software.testing.usermodule.mapper",
        "com.software.testing.coursemodule.mapper",
        "com.software.testing.taskmodule.mapper",
        "com.software.testing.submitmodule.mapper",
        "com.software.testing.scoremodule.mapper"})
public class TeachingPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(TeachingPlatformApplication.class, args);
    }
}
