package com.io.springbatch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BatchStatusExitStatusConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job batchStatusExitStatusJob1() {
        return jobBuilderFactory.get("batchStatusExitStatusJob1")
                .start(batchStatusExitStatusStep1())
                .next(batchStatusExitStatusStep2())
                .build();
    }

    @Bean
    public Job batchStatusExitStatusJob2() {
        return jobBuilderFactory.get("batchStatusExitStatusJob2")
                .start(batchStatusExitStatusStep1())
                .on("FAILED")
                .to(batchStatusExitStatusStep2())
                .end()
                .build();
    }

    @Bean
    public Step batchStatusExitStatusStep1() {
        return stepBuilderFactory.get("batchStatusExitStatusStep1")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("batchStatusExitStatusStep1 has executed");

                        stepContribution.setExitStatus(ExitStatus.FAILED);

                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }

    @Bean
    public Step batchStatusExitStatusStep2() {
        return stepBuilderFactory.get("batchStatusExitStatusStep2")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("batchStatusExitStatusStep2 has executed");

//                        stepContribution.setExitStatus(ExitStatus.FAILED);

                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }
}
