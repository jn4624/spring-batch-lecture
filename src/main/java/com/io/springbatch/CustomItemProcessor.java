package com.io.springbatch;

import org.modelmapper.ModelMapper;
import org.springframework.batch.item.ItemProcessor;

public class CustomItemProcessor implements ItemProcessor<Customer, Customer2> {
    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public Customer2 process(Customer customer) throws Exception {
        Customer2 customer2 = modelMapper.map(customer, Customer2.class);
        return customer2;
    }
}
