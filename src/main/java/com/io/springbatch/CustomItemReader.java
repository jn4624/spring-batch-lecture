package com.io.springbatch;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.ArrayList;
import java.util.List;

public class CustomItemReader implements ItemReader<Customer> {
    private List<Customer> customers;

    public CustomItemReader(List<Customer> customers) {
        this.customers = new ArrayList<>(customers);
    }

    @Override
    public Customer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (!customers.isEmpty()) {
            return customers.remove(0);
        }
        return null;
    }
}
