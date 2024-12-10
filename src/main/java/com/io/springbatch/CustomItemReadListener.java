package com.io.springbatch;

import org.springframework.batch.core.ItemReadListener;

public class CustomItemReadListener implements ItemReadListener<Customer> {
    @Override
    public void beforeRead() {

    }

    @Override
    public void afterRead(Customer customer) {
        System.out.println("thread name: " + Thread.currentThread().getName() +
                ", read item: " + customer.getId());
    }

    @Override
    public void onReadError(Exception e) {

    }
}
