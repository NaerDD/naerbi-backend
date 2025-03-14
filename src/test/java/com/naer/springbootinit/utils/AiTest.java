package com.naer.springbootinit.utils;

import com.naer.springbootinit.manager.AiManager;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author NaerDD
 * @version 1.0
 **/
@SpringBootTest
public class AiTest {
    @Resource
    private AiManager aiManager;
    @Test
    public void test(){
        String ANIME = aiManager.doChat("推荐10部日本动画");
        System.out.println(ANIME);
    }
}
