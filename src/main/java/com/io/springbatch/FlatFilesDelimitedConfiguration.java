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
import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class FlatFilesDelimitedConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job flatFilesDelimitedJob() {
        return jobBuilderFactory.get("flatFilesDelimitedJob")
                .incrementer(new RunIdIncrementer())
                .start(flatFilesDelimitedStep1())
                .build();
    }

    @Bean
    public Step flatFilesDelimitedStep1() {
        return stepBuilderFactory.get("flatFilesDelimitedStep1")
                .<Customer, Customer>chunk(10)
                .reader(customFlatFilesDelimitedItemReader())
                .writer(customFlatFilesDelimitedItemWriter())
                .build();
    }

    @Bean
    public ItemReader<? extends Customer> customFlatFilesDelimitedItemReader() {
        List<Customer> customers = Arrays.asList(new Customer(1, "hong gil dong1", 31),
                new Customer(2, "hong gil dong2", 32),
                new Customer(3, "hong gil dong3", 33));

        return new ListItemReader<>(customers);
//        return new ListItemReader<>(Collections.EMPTY_LIST);
    }

    @Bean
    public ItemWriter<? super Customer> customFlatFilesDelimitedItemWriter() {
        return new FlatFileItemWriterBuilder<>()
                .name("flatFileItemWriter")
                .resource(new FileSystemResource("/Users/jinalim/git/spring-batch-lecture/src/main/resources/customer1.txt"))
                .append(true) // 이미 생성된 파일에 내용 추가
                .shouldDeleteIfEmpty(true) // 기록할 데이터가 없는 경우 파일 삭제
                .delimited()
                .delimiter("|")
                .names(new String[]{"id", "name", "age"})
                .build();
    }
}
