package com.springbatch.job;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Data
@Configuration  //모든 Job은 @Configuration으로 등록
public class SimpleNextJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job nextJob(){
        return jobBuilderFactory.get("nextJob")       //Job 의 이름을 설정.
                .start(step1(null))   //Step 을 지정.(JobParameters 로 지정 시에 program argument 에 넣어주면 된다.) //그렇다면 지금넣어준 값은? 아무의미가 없는 값이다.(argument 에만 반응한다.)
                .next(step2())
                .next(step3())
                .build();
    }
    @Bean
    @JobScope
    public Step step1(@Value("#{jobParameters[requestDate]}")String requestDate){
        return stepBuilderFactory.get("step1")                //Step 의 이름을 설정.
                .tasklet(((stepContribution, chunkContext) -> {
                    log.info("::>>>> This is Step1");
                    log.info("::>>>> RequestDate :{}",requestDate);
                    return RepeatStatus.FINISHED;
                })).build();
    }
    @Bean
    public Step step2(){
        return stepBuilderFactory.get("step2")                //Step 의 이름을 설정.
                .tasklet(((stepContribution, chunkContext) -> {
                    log.info("::>>>> This is Step2");
                    return RepeatStatus.FINISHED;
                })).build();
    }
    @Bean
    public Step step3(){
        return stepBuilderFactory.get("step3")                //Step 의 이름을 설정.
                .tasklet(((stepContribution, chunkContext) -> {
                    log.info("::>>>> This is Step3");
                    return RepeatStatus.FINISHED;
                })).build();
    }

}
