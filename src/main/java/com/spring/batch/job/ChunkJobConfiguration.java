package com.spring.batch.job;

import com.spring.batch.jpa.entity.HistoryEntity;
import com.spring.batch.jpa.entity.SenderEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.persistence.EntityManagerFactory;

@Slf4j
@Data
@Configuration
public class ChunkJobConfiguration {
    //ChunkOrientedTasklet
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory managerFactory;

    public static final String JOB_NAME = "chunkJob";
    public static final String STEP_NAME = "chunkStep";

    @Bean
    public Job chunkJob(){
        return jobBuilderFactory.get(JOB_NAME)
                .start(chunkStep())
                .build();
    }
    private Step chunkStep(){
        return stepBuilderFactory.get(STEP_NAME)
                .<SenderEntity, HistoryEntity> chunk(100)    // I•O 지정 하지 않을 시에는 에러가 날 수 있으므로 지정하자.
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }
    private JpaPagingItemReader<? extends SenderEntity> reader(){
        JpaPagingItemReader<SenderEntity> reader = new JpaPagingItemReader<>();
        reader.setName("JpaPagingItemReader");
        reader.setQueryString("Select o from SenderEntity o");
        reader.setEntityManagerFactory(managerFactory);
        reader.setPageSize(100);
        return reader;
    }

    private ItemProcessor<SenderEntity, HistoryEntity> processor(){
        return senderEntity -> HistoryEntity.builder()
                    .senderId(senderEntity.getId())
                    .moneyEntityList(senderEntity.getMoneyEntityList())
                    .build();
    }
    private JpaItemWriter<HistoryEntity> writer(){
        JpaItemWriter<HistoryEntity> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(managerFactory);
        return writer;
    }


}
