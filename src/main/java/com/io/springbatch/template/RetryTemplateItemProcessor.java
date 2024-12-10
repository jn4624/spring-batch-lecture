package com.io.springbatch.template;

import com.io.springbatch.RetryableException;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.classify.Classifier;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.DefaultRetryState;
import org.springframework.retry.support.RetryTemplate;

public class RetryTemplateItemProcessor implements ItemProcessor<String, Customer> {
    @Autowired
    private RetryTemplate retryTemplate;
    private int cnt = 0;

    @Override
    public Customer process(String s) throws Exception {
        Classifier<Throwable, Boolean> rollbackClassifier = new BinaryExceptionClassifier(true);

        Customer customer = retryTemplate.execute(new RetryCallback<Customer, RuntimeException>() {
            @Override
            public Customer doWithRetry(RetryContext retryContext) throws RuntimeException {
                if (s.equals("1") || s.equals("2")) {
                    cnt++;
                    throw new RetryableException("process failed cnt: " + cnt);
                }
                return new Customer(s);
            }
        }, new RecoveryCallback<Customer>() {
            @Override
            public Customer recover(RetryContext retryContext) throws Exception {
                return new Customer(s);
            }
        }, new DefaultRetryState(s, rollbackClassifier));
        return customer;
    }
}
