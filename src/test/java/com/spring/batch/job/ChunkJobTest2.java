package com.spring.batch.job;

import com.spring.batch.TestBatchLegacyConfig;
import com.spring.batch.jpa.entity.MoneyEntity;
import com.spring.batch.jpa.entity.SenderEntity;
import com.spring.batch.jpa.repository.SenderRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@Slf4j
@SpringBatchTest
@SpringBootTest(
        properties = "job.name="+ChunkJobConfiguration.JOB_NAME,
        classes = {TestBatchLegacyConfig.class, ChunkJobConfiguration.class}
)
public class ChunkJobTest2 {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils; // TEST시 사용

    @Autowired
    private SenderRepository repository;

    @Test
    public void chunkTest() throws Exception {
        //given
        for(int i = 0 ; i<100 ; i++){
            repository.save(
                    SenderEntity.builder()
                        .name("WCJEONG")
                        .moneyEntityList(
                                Arrays.asList(
                                        MoneyEntity.builder().value("10000").build(),
                                        MoneyEntity.builder().value("20000").build(),
                                        MoneyEntity.builder().value("30000").build()
                                )
                        ).build()
            );
        }
        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        //then
        Assertions.assertEquals(100,repository.findAll().size());
        Assertions.assertEquals(BatchStatus.COMPLETED,jobExecution.getStatus());

    }

}
