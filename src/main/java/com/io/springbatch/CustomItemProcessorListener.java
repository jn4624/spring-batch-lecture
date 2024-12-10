package com.io.springbatch;

import org.springframework.batch.core.ItemProcessListener;

public class CustomItemProcessorListener implements ItemProcessListener<Customer, Customer> {
    @Override
    public void beforeProcess(Customer customer) {

    }

    @Override
    public void afterProcess(Customer customer, Customer customer2) {
        System.out.println("thread name: " + Thread.currentThread().getName() +
                ", process item: " + customer.getId());
    }

    @Override
    public void onProcessError(Customer customer, Exception e) {

    }
}
