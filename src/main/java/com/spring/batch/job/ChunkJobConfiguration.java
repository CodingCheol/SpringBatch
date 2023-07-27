package com.spring.batch.job;

import com.spring.batch.jpa.entity.HistoryEntity;
import com.spring.batch.jpa.entity.MoneyEntity;
import com.spring.batch.jpa.entity.SenderEntity;
import com.spring.batch.jpa.repository.HistoryRepository;
import com.spring.batch.jpa.repository.SenderRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.stream.IntStream;

@Slf4j
@Data
@Configuration
public class ChunkJobConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final EntityManagerFactory managerFactory;
    private final SenderRepository senderRepository;
    private final HistoryRepository historyRepository;

    public static final String JOB_NAME = "chunkJob";
    public static final String STEP_NAME = "chunkStep";

    @Bean
    public Job chunkJob(){
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(saveStep())
                .next(chunkStep(null))
                .next(validStep())
                .build();
    }

    @Bean
    public Step saveStep() {
        return new StepBuilder("saveStep", jobRepository)
                .tasklet((a, b) -> {
                    var senderEntities = IntStream.range(1, 10).mapToObj(i ->
                            SenderEntity.builder()
                                    .name(String.format("%d-Sender", i))
                                    .moneyEntityList(IntStream.range(1, 10).mapToObj(j -> MoneyEntity.builder().value(i * j * 100).build()).toList())
                                    .build()).toList();
                    senderRepository.saveAll(senderEntities);
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    @JobScope
    public Step chunkStep(@Value("#{jobParameters['chunkSize']}") Integer chunkSize){
        //ChunkOrientedTasklet
        return new StepBuilder(STEP_NAME, jobRepository)
                .<SenderEntity, HistoryEntity> chunk(chunkSize, platformTransactionManager)
                .reader(reader(chunkSize))
                .processor(processor())
                .writer(writer())
                .build();
    }
    @Bean
    @StepScope
    public JpaPagingItemReader<? extends SenderEntity> reader(@Value("#{jobParameters['pageSize']}") Integer pageSize){
        return new JpaPagingItemReaderBuilder<SenderEntity>()
                .name("JpaPagingItemReader")
                .queryString("Select o from SenderEntity o")
                .pageSize(pageSize)
                .entityManagerFactory(managerFactory)
                .build();
        /*
        JpaPagingItemReader<SenderEntity> reader = new JpaPagingItemReader<>();
        reader.setName("JpaPagingItemReader");
        reader.setQueryString("Select o from SenderEntity o");
        reader.setEntityManagerFactory(managerFactory);
        reader.setPageSize(pageSize);
        return reader;
        */
    }

    @Bean
    public ItemProcessor<SenderEntity, HistoryEntity> processor(){
        return senderEntity -> HistoryEntity.builder()
                    .senderId(senderEntity.getId())
                    .moneyEntityList(senderEntity.getMoneyEntityList())
                    .build();
    }

    @Bean
    public JpaItemWriter<HistoryEntity> writer(){
        JpaItemWriter<HistoryEntity> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(managerFactory);
        return writer;
    }

    @Bean
    public Step validStep() {
        return new StepBuilder("lastStep", jobRepository)
                .tasklet((a, b) -> {
                    var histories = historyRepository.findAll();
                    log.info("test {}", histories);
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }


}
