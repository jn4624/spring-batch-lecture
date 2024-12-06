package com.io.springbatch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SimpleFlowConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job simpleFlowJob() {
        return jobBuilderFactory.get("simpleFlowJob")
                .start(simpleFlow1())
                    .on("COMPLETED")
                    .to(simpleFlow2())
                .from(simpleFlow1())
                    .on("FAILED")
                    .to(simpleFlow3())
                .end()
                .build();
    }

    @Bean
    public Flow simpleFlow1() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("simpleFlow1");
        flowBuilder.start(simpleFlowStep1())
                .next(simpleFlowStep2())
                .end();
        return flowBuilder.build();
    }

    @Bean
    public Flow simpleFlow2() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("simpleFlow2");
        flowBuilder.start(simpleFlow3())
                .next(simpleFlowStep5())
                .next(simpleFlowStep6())
                .end();
        return flowBuilder.build();
    }

    @Bean
    public Flow simpleFlow3() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("simpleFlow3");
        flowBuilder.start(simpleFlowStep3())
                .next(simpleFlowStep4())
                .end();
        return flowBuilder.build();
    }

    @Bean
    public Step simpleFlowStep1() {
        return stepBuilderFactory.get("simpleFlowStep1")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("simpleFlowStep1 has executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }

    @Bean
    public Step simpleFlowStep2() {
        return stepBuilderFactory.get("simpleFlowStep2")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("simpleFlowStep2 has executed");
                        throw new RuntimeException("simpleFlowStep2 failed");
//                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }

    @Bean
    public Step simpleFlowStep3() {
        return stepBuilderFactory.get("simpleFlowStep3")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("simpleFlowStep3 has executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }

    @Bean
    public Step simpleFlowStep4() {
        return stepBuilderFactory.get("simpleFlowStep4")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("simpleFlowStep4 has executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }

    @Bean
    public Step simpleFlowStep5() {
        return stepBuilderFactory.get("simpleFlowStep5")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("simpleFlowStep5 has executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }

    @Bean
    public Step simpleFlowStep6() {
        return stepBuilderFactory.get("simpleFlowStep6")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("simpleFlowStep6 has executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }
}
