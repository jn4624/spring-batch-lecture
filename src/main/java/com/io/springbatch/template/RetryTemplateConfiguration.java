package com.io.springbatch.template;

import com.io.springbatch.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class RetryTemplateConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job retryTemplateJob() {
        return jobBuilderFactory.get("retryTemplateJob")
                .incrementer(new RunIdIncrementer())
                .start(retryTemplateStep1())
                .build();
    }

    @Bean
    public Step retryTemplateStep1() {
        return stepBuilderFactory.get("retryTemplateStep1")
                .<String, Customer>chunk(5)
                .reader(retryTemplateItemReader())
                .processor(retryTemplateItemProcessor())
                .writer(new ItemWriter<Customer>() {
                    @Override
                    public void write(List<? extends Customer> list) throws Exception {
                        list.forEach(item -> System.out.println(item));
                    }
                })
                .faultTolerant()
                .skip(RetryableException.class)
                .skipLimit(2)
                .build();
    }

    @Bean
    public ItemReader<String> retryTemplateItemReader() {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            items.add(String.valueOf(i));
        }
        return new ListItemReader<>(items);
    }

    @Bean
    public ItemProcessor<? super String, ? extends Customer> retryTemplateItemProcessor() {
        return new RetryTemplateItemProcessor();
    }

    @Bean
    public RetryTemplate retryTemplate() {
        Map<Class<? extends Throwable>, Boolean> exceptionClass = new HashMap<>();
        exceptionClass.put(RetryableException.class, true);

        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(2000);

        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(2, exceptionClass);

        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(simpleRetryPolicy);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        return retryTemplate;
    }
}
