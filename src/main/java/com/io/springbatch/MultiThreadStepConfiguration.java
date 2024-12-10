package com.io.springbatch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.*;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class MultiThreadStepConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job job() {
        return jobBuilderFactory.get("job")
                .incrementer(new RunIdIncrementer())
                .start(syncStep())
                .listener(new StopWatchJobListener())
                .build();
    }

    /*
      단일 스레드 (data: 7136)
      *****************************************
      총 소요시간: 1544
      *****************************************

      멀티 스레드 (data: 7136)
      *****************************************
      총 소요시간: 543
      *****************************************
     */
    @Bean
    public Step syncStep() {
        return stepBuilderFactory.get("syncStep")
                .<Customer, Customer>chunk(100)
                .reader(customItemReader())
                .listener(new CustomItemReadListener())
                .processor((ItemProcessor<Customer, Customer>) item -> item)
                .listener(new CustomItemProcessorListener())
                .writer(customItemWriter())
                .listener(new CustomItemWriterListener())
//                .taskExecutor(new SimpleAsyncTaskExecutor()) // 간단하게 사용할 때 적용
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(4);
        taskExecutor.setMaxPoolSize(8);
        taskExecutor.setThreadNamePrefix("async-thread");
        return taskExecutor;
    }

    @Bean
    public ItemReader<Customer> customItemReader() {
        // thread-safe
        JdbcPagingItemReader<Customer> itemReader = new JdbcPagingItemReader<>();
        itemReader.setDataSource(dataSource);
        itemReader.setFetchSize(300);
        itemReader.setRowMapper(new CustomerRowMapper());

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("id, firstName, lastName, birthdate");
        queryProvider.setFromClause("from customer");

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);

        itemReader.setQueryProvider(queryProvider);

        return itemReader;

        // non-thread-safe
//        return new JdbcCursorItemReaderBuilder<Customer>()
//                .name("jdbcCursorItemReader")
//                .fetchSize(100)
//                .sql("select id, firstName, lastName, birthdate from customer order by id")
//                .beanRowMapper(Customer.class)
//                .dataSource(dataSource)
//                .build();
    }

    @Bean
    public JdbcBatchItemWriter customItemWriter() {
        JdbcBatchItemWriter<Object> itemWriter = new JdbcBatchItemWriter<>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("insert into customer2 values (:id, :firstName, :lastName, :birthdate)");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        itemWriter.afterPropertiesSet();
        return itemWriter;
    }
}
