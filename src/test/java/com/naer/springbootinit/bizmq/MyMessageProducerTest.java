package com.naer.springbootinit.bizmq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author NaerDD
 * @version 1.0
 **/
@SpringBootTest
class MyMessageProducerTest {
    @Resource
    private BiMessageProducer biMessageProducer;

    @Test
    void sendMessage() {

    }
}