package com.likc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
class VueblogApplicationTests {

    @Test
    void contextLoads() {

        System.out.println(LocalDateTime.now());

    }

}
