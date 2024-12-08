package com.io.springbatch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class FlatFilesConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job flatFilesJob() {
        return jobBuilderFactory.get("flatFilesJob")
                .start(flatFilesStep1())
                .next(flatFilesStep2())
                .build();
    }

    @Bean
    public Step flatFilesStep1() {
        return stepBuilderFactory.get("flatFilesStep1")
                .<String, String>chunk(5)
                .reader(flatFilesItemReader())
                .writer(new ItemWriter<String>() {
                    @Override
                    public void write(List<? extends String> list) throws Exception {
                        System.out.println("list = " + list);
                    }
                })
                .build();
    }

    @Bean
    public ItemReader flatFilesItemReader() {
        FlatFileItemReader<Customer> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new ClassPathResource("/customer.csv"));

        DefaultLineMapper<Customer> defaultLineMapper = new DefaultLineMapper<>();
        defaultLineMapper.setLineTokenizer(new DelimitedLineTokenizer());
        defaultLineMapper.setFieldSetMapper(new CustomerFieldSetMapper());

        flatFileItemReader.setLineMapper(defaultLineMapper);
        flatFileItemReader.setLinesToSkip(1);
        return flatFileItemReader;
    }

    @Bean
    public Step flatFilesStep2() {
        return stepBuilderFactory.get("flatFilesStep2")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("flatFilesStep2 has executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }
}
