package com.io.springbatch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.skip.LimitCheckingItemSkipPolicy;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SkipConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job skipJob() {
        return jobBuilderFactory.get("skipJob")
                .incrementer(new RunIdIncrementer())
                .start(skipStep1())
                .build();
    }

    @Bean
    public Step skipStep1() {
        return stepBuilderFactory.get("skipStep1")
                .<String, String>chunk(5)
                .reader(new ItemReader<String>() {
                    int i = 0;
                    @Override
                    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        i++;
                        if (i == 3) {
//                            throw new SkippableException("skip");
                            throw new NoSkippableException("skip");
                        }

                        System.out.println("itemReader item: " + i);
                        return i > 20 ? null : String.valueOf(i);
                    }
                })
                .processor(customItemProcessor())
                .writer(customItemWriter())
                .faultTolerant()
//                .skip(SkippableException.class)
//                .skipLimit(4)
//                .skipPolicy(limitCheckingItemSkipPolicy())
//                .skipPolicy(new AlwaysSkipItemSkipPolicy())
//                .skipPolicy(new NeverSkipItemSkipPolicy())
                .noSkip(NoSkippableException.class)
                .build();
    }

    @Bean
    public ItemProcessor<? super String, String> customItemProcessor() {
        return new SkipItemProcessor();
    }

    @Bean
    public ItemWriter<? super String> customItemWriter() {
        return new SkipItemWriter();
    }

    @Bean
    public SkipPolicy limitCheckingItemSkipPolicy() {
        Map<Class<? extends Throwable>, Boolean> exceptionClass = new HashMap<>();
        exceptionClass.put(SkippableException.class, true);

        LimitCheckingItemSkipPolicy limitCheckingItemSkipPolicy = new LimitCheckingItemSkipPolicy(4, exceptionClass);

        return limitCheckingItemSkipPolicy;
    }
}
