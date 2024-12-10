package com.io.springbatch;

import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class SkipItemWriter implements ItemWriter<String> {
    private int cnt = 0;

    @Override
    public void write(List<? extends String> list) throws Exception {
        for (String s : list) {
            if (s.equals("-12")) {
                System.out.println("itemWriter item: " + s);
                throw new SkippableException("write failed. skip cnt: " + cnt);
            } else {
                System.out.println("itemWriter item: " + s);
            }
        }
    }
}
