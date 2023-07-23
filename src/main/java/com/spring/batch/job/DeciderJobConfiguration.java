package com.spring.batch.job;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Slf4j
@Configuration
@Data
public class DeciderJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job deciderJob(){
        return jobBuilderFactory.get("deciderJob")
                .start(startStep())
                .next(decider())
                .from(decider())
                    .on("ODD")
                    .to(oddStep())
                .from(decider())
                    .on("EVEN")
                    .to(evenStep())
                .end()
                .build();
    }
    @Bean
    public Step startStep(){
        return stepBuilderFactory.get("startStep")
                .tasklet((StepContribution var1, ChunkContext var2)->{
                    log.info("::>>>> This is StartStep");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
    @Bean
    public JobExecutionDecider decider(){
        return (JobExecution jobExecution, StepExecution stepExecution)->{
            Random rand = new Random();

            int randomNumber = rand.nextInt(50)+1;
            log.info("RandomNumber : {}",randomNumber);

            if(randomNumber %2 ==0){
                return new FlowExecutionStatus("EVEN");
            }
            return new FlowExecutionStatus("ODD");
        };
    }
    @Bean
    public Step oddStep() {
        return stepBuilderFactory.get("oddStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info("::>>>>> This is oddStep.");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
    @Bean
    public Step evenStep() {
        return stepBuilderFactory.get("evenStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info("::>>>>> This is evenStep.");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

}
