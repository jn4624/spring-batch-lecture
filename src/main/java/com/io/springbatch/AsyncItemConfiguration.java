package com.io.springbatch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class AsyncItemConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job job() throws InterruptedException {
        return jobBuilderFactory.get("job")
                .incrementer(new RunIdIncrementer())
                .start(asyncStep())
                .listener(new StopWatchJobListener())
                .build();
    }

    /*
      *****************************************
      총 소요시간: 3425
      *****************************************
     */
    @Bean
    public Step syncStep() throws InterruptedException {
        return stepBuilderFactory.get("syncStep")
                .<Customer, Customer>chunk(100)
                .reader(customItemReader())
                .processor(customItemProcessor())
                .writer(customItemWriter())
                .build();
    }

    /*
      *****************************************
      총 소요시간: 117
      *****************************************
     */
    @Bean
    public Step asyncStep() throws InterruptedException {
        return stepBuilderFactory.get("asyncStep")
                .<Customer, Customer>chunk(100)
                .reader(customItemReader())
                .processor(asyncItemProcessor())
                .writer(asyncItemWriter())
                .build();
    }

    @Bean
    public AsyncItemProcessor asyncItemProcessor() throws InterruptedException {
        AsyncItemProcessor<Customer, Customer> itemProcessor = new AsyncItemProcessor<>();
        itemProcessor.setDelegate(customItemProcessor());
        itemProcessor.setTaskExecutor(new SimpleAsyncTaskExecutor());
//        itemProcessor.afterPropertiesSet(); // Bean 객체로 생성하지 않았을 때는 선언 필요
        return itemProcessor;
    }

    @Bean
    public AsyncItemWriter asyncItemWriter() {
        AsyncItemWriter<Customer> itemWriter = new AsyncItemWriter<>();
        itemWriter.setDelegate(customItemWriter());
        return itemWriter;
    }

    @Bean
    public ItemReader<Customer> customItemReader() {
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
    }

    @Bean
    public ItemProcessor<Customer, Customer> customItemProcessor() throws InterruptedException {
        return new ItemProcessor<Customer, Customer>() {
            @Override
            public Customer process(Customer o) throws Exception {
                Thread.sleep(30);

                return new Customer(o.getId(),
                        o.getFirstName().toUpperCase(),
                        o.getLastName().toUpperCase(),
                        o.getBirthdate());
            }
        };
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
