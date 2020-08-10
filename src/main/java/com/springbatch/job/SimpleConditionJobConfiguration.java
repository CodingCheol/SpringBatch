package com.springbatch.job;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
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
public class SimpleConditionJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job conditionJob(){
        return jobBuilderFactory.get("conditionJob")       //Job 의 이름을 설정.
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
    //이 아래 소스로 argument 값으로 조절해볼려고 했지만, 잘되지 않았다. 왜 일까?
    // error : singleton bean creation not allowed while singletons of this factory are is destruction(Do not request a bean from a BeanFactory in a destroy method implementation!)
    // 즉, 싱글톤 객체를 파라미터만으로 다른 Step 을 탈 수 있게 만드는 것은 절대적으로 불가능하다라는 것이다. 그리고 요청 갑승로 빈을 다시 만드는게 안돼! 뭐 이런뜻..
   /* @Bean
    @JobScope
    public Step conditionJobStep(@Value("#{jobParameters[booleans]}")String bool){
        return stepBuilderFactory.get("conditionJobStep")                //Step 의 이름을 설정.
                .tasklet(((stepContribution, chunkContext) -> {
                    log.info("::>>>> This is conditionJobStep");
                    log.info("::>>>> Condition :{}",bool);
                    if( Boolean.parseBoolean(bool) ){
                        stepContribution.setExitStatus(ExitStatus.COMPLETED);
                    }else{
                        stepContribution.setExitStatus(ExitStatus.FAILED);
                    }
                    return RepeatStatus.FINISHED;
                })).build();
    }*/
    @Bean
    public Step conditionJobStep(){
        return stepBuilderFactory.get("conditionJobStep")                //Step 의 이름을 설정.
                .tasklet(((stepContribution, chunkContext) -> {
                    log.info("::>>>> This is conditionJobStep");
                    stepContribution.setExitStatus(ExitStatus.FAILED);
                    return RepeatStatus.FINISHED;
                })).build();
    }
    @Bean
    public Step failJobStep(){
        return stepBuilderFactory.get("failJobStep")                //Step 의 이름을 설정.
                .tasklet(((stepContribution, chunkContext) -> {
                    log.info("::>>>> This is failJobStep");
                    return RepeatStatus.FINISHED;
                })).build();
    }
    @Bean
    public Step completedJobStep(){
        return stepBuilderFactory.get("completedJobStep")                //Step 의 이름을 설정.
                .tasklet(((stepContribution, chunkContext) -> {
                    log.info("::>>>> This is completedJobStep");
                    return RepeatStatus.FINISHED;
                })).build();
    }
    @Bean
    public Step completedNextJobStep(){
        return stepBuilderFactory.get("completedNextJobStep")                //Step 의 이름을 설정.
                .tasklet(((stepContribution, chunkContext) -> {
                    log.info("::>>>> This is completedNextJobStep");
                    return RepeatStatus.FINISHED;
                })).build();
    }

}
