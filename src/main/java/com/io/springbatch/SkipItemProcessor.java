package com.io.springbatch;

import org.springframework.batch.item.ItemProcessor;

public class SkipItemProcessor implements ItemProcessor<String, String> {
    private int cnt = 0;

    @Override
    public String process(String s) throws Exception {
        if (s.equals("6") || s.equals("7")) {
            System.out.println("itemProcessor item: " + s);
            throw new SkippableException("process failed. skip cnt: " + cnt);
        } else {
            System.out.println("itemProcessor item: " + s);
            return String.valueOf(Integer.valueOf(s) * -1);
        }
    }
}
