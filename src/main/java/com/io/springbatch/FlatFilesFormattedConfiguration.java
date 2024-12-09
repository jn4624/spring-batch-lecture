package com.io.springbatch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class FlatFilesFormattedConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job flatFilesFormattedJob() {
        return jobBuilderFactory.get("flatFilesFormattedJob")
                .incrementer(new RunIdIncrementer())
                .start(flatFilesFormattedStep1())
                .build();
    }

    @Bean
    public Step flatFilesFormattedStep1() {
        return stepBuilderFactory.get("flatFilesFormattedStep1")
                .<Customer, Customer>chunk(10)
                .reader(customFlatFilesFormattedItemReader())
                .writer(customFlatFilesFormattedItemWriter())
                .build();
    }

    @Bean
    public ItemReader<? extends Customer> customFlatFilesFormattedItemReader() {
        List<Customer> customers = Arrays.asList(new Customer(1, "hong gil dong1", 31),
                new Customer(2, "hong gil dong2", 32),
                new Customer(3, "hong gil dong3", 33));

        return new ListItemReader<>(customers);
    }

    @Bean
    public ItemWriter<? super Customer> customFlatFilesFormattedItemWriter() {
        return new FlatFileItemWriterBuilder<>()
                .name("flatFileItemWriter")
                .resource(new FileSystemResource("/Users/jinalim/git/spring-batch-lecture/src/main/resources/customer2.txt"))
                .append(true)
                .formatted()
                .format("%-2d%-15s%-2d")
                .names(new String[]{"id", "name", "age"})
                .build();
    }
}
