package com.spring.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class NextJobConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job nextJob() {
        return new JobBuilder("nextJob", jobRepository)
                .start(step1(null))
                .next(step2())
                .next(step3())
                .build();
    }

    @Bean
    @JobScope
    public Step step1(@Value("#{jobParameters['requestDate']}") String requestDate) {
        return new StepBuilder("step1", jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    log.info("::>>>> This is Step1");
                    log.info("::>>>> RequestDate :{}", requestDate);
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager).build();
    }

    @Bean
    public Step step2() {
        return new StepBuilder("step2", jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    log.info("::>>>> This is Step2");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager).build();
    }

    @Bean
    public Step step3() {
        return new StepBuilder("step3", jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    log.info("::>>>> This is Step3");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager).build();
    }

}
