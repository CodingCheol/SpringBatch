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

import java.time.LocalDateTime;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SimpleJobConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job simpleJob(){
        return new JobBuilder("simpleJob", jobRepository) //Job 의 이름을 설정.
                //Step 을 지정.(JobParameters 로 지정 시에 program argument 에 넣어주면 된다.)
                // 그렇다면 지금넣어준 값은? 아무의미가 없는 값이다.(argument 에만 반응한다.)
                .start(SimpleStep1(null))
                .build();
    }
    @Bean
    @JobScope
    public Step SimpleStep1(@Value("#{jobParameters['requestDate']?:T(java.time.LocalDateTime).now()}") LocalDateTime requestDate) {
        return new StepBuilder("simpleStep1", jobRepository) //Step 의 이름을 설정.
                .tasklet((stepContribution, chunkContext) -> {
                    //Step 내부의 Tasklet 을 지정.
                    // Tasklet : Step 안에서 단일로 수행될 커스텀한 기능 수행.
                    // Reader & (Processor) & Writer 묶음도 존재.
                    log.info("::>>>> This is SimpleStep1");
                    log.info("::>>>> RequestDate :{}", requestDate);
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager).build();
    }

}
