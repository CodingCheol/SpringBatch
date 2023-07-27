package com.spring.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ConditionJobConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job conditionJob() {
        return new JobBuilder("conditionJob", jobRepository)
                .start(conditionJobStep())
                    .on(ExitStatus.FAILED.getExitCode())
                    .to(failJobStep())
                    .on("*")
                    .end()
                .from(conditionJobStep())
                    .on(ExitStatus.COMPLETED.getExitCode())
                    .to(completedJobStep())
                .next(completedNextJobStep())
                    .on("*")
                    .end()
                .end()
                .build();
    }

    @Bean
    public Step conditionJobStep() {
        return new StepBuilder("conditionJobStep", jobRepository)                //Step 의 이름을 설정.
                .tasklet((stepContribution, chunkContext) -> {
                    log.info("::>>>> This is conditionJobStep");
                    stepContribution.setExitStatus(ExitStatus.FAILED);
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager).build();
    }

    @Bean
    public Step failJobStep() {
        return new StepBuilder("failJobStep", jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    log.info("::>>>> This is failJobStep");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager).build();
    }

    @Bean
    public Step completedJobStep() {
        return new StepBuilder("completedJobStep", jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    log.info("::>>>> This is completedJobStep");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager).build();
    }

    @Bean
    public Step completedNextJobStep() {
        return new StepBuilder("completedNextJobStep", jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    log.info("::>>>> This is completedNextJobStep");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager).build();
    }

}
