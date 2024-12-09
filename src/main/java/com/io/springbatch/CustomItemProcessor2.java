package com.io.springbatch;

import org.springframework.batch.item.ItemProcessor;

public class CustomItemProcessor2 implements ItemProcessor<String, String> {
    int cnt = 0;

    @Override
    public String process(String s) throws Exception {
        cnt++;
        return s + cnt;
    }
}
