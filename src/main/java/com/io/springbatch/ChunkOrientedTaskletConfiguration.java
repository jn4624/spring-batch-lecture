package com.io.springbatch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ChunkOrientedTaskletConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job chunkOrientedTaskletJob() {
        return jobBuilderFactory.get("chunkOrientedTaskletJob")
                .start(chunkOrientedTaskletStep1())
                .next(chunkOrientedTaskletStep2())
                .build();
    }

    @Bean
    public Step chunkOrientedTaskletStep1() {
        return stepBuilderFactory.get("chunkOrientedTaskletStep1")
                .<String, String>chunk(2)
                .reader(new ListItemReader<>(Arrays.asList("item1", "item2", "item3", "item4", "item5", "item6", "item7")))
                .processor(new ItemProcessor<String, String>() {
                    @Override
                    public String process(String s) throws Exception {
                        return "my-" + s;
                    }
                })
                .writer(new ItemWriter<String>() {
                    @Override
                    public void write(List<? extends String> list) throws Exception {
                        list.forEach(item -> System.out.println(item));
                    }
                })
                .build();
    }

    @Bean
    public Step chunkOrientedTaskletStep2() {
        return stepBuilderFactory.get("chunkOrientedTaskletStep2")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("chunkOrientedTaskletStep2 has executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }
}
