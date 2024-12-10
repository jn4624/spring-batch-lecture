package com.io.springbatch;

import org.springframework.batch.core.ItemWriteListener;

import java.util.List;

public class CustomItemWriterListener implements ItemWriteListener<Customer> {
    @Override
    public void beforeWrite(List<? extends Customer> list) {

    }

    @Override
    public void afterWrite(List<? extends Customer> list) {
        System.out.println("thread name: " + Thread.currentThread().getName() +
                ", write item: " + list.size());
    }

    @Override
    public void onWriteError(Exception e, List<? extends Customer> list) {

    }
}
