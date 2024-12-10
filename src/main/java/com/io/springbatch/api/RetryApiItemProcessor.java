package com.io.springbatch.api;

import com.io.springbatch.RetryableException;
import org.springframework.batch.item.ItemProcessor;

public class RetryApiItemProcessor implements ItemProcessor<String, String> {
    private int cnt = 0;

    @Override
    public String process(String s) throws Exception {
        if (s.equals("2") || s.equals("3")) {
            cnt++;
            throw new RetryableException("processor failed cnt: " + cnt);
        }
        return s;
    }
}
