package com.io.springbatch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class FixedLengthTokenizerConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job fixedLengthTokenizerJob() {
        return jobBuilderFactory.get("fixedLengthTokenizerJob")
                .start(fixedLengthTokenizerStep1())
                .next(fixedLengthTokenizerStep2())
                .build();
    }

    @Bean
    public Step fixedLengthTokenizerStep1() {
        return stepBuilderFactory.get("fixedLengthTokenizerStep1")
                .<String, String>chunk(5)
                .reader(fixedLengthTokenizerItemReader())
                .writer(new ItemWriter<String>() {
                    @Override
                    public void write(List<? extends String> list) throws Exception {
                        System.out.println("list = " + list);
                    }
                })
                .build();
    }

    @Bean
    public FlatFileItemReader fixedLengthTokenizerItemReader() {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("flatFile")
                .resource(new FileSystemResource("/Users/jinalim/git/spring-batch-lecture/src/main/resources/customer.txt"))
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>())
                .targetType(Customer.class)
                .linesToSkip(1)
                .fixedLength()
                // ex: Customer(name=user1, age=30, year=2000)
                .addColumns(new Range(1, 5))
                .addColumns(new Range(6, 9))
                .addColumns(new Range(10, 11))
                // ex: Customer(name=user1200030, age=30, year=200030)
//                .addColumns(new Range(1))
//                .addColumns(new Range(6))
//                .addColumns(new Range(10))
                .names("name", "year", "age")
                .build();
    }

    @Bean
    public Step fixedLengthTokenizerStep2() {
        return stepBuilderFactory.get("fixedLengthTokenizerStep2")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("fixedLengthTokenizerStep2 has executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }
}
