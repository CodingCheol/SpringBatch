package com.springbatch.job;

import com.springbatch.jpa.entity.MoneyEntity;
import com.springbatch.jpa.entity.SenderEntity;
import com.springbatch.jpa.repository.SenderRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import java.util.Arrays;

@Slf4j
@SpringBootTest(
        properties = "job.name="+ChunkJobConfiguration.JOB_NAME
)
public class ChunkJobTest {

    private JobLauncherTestUtils jobLauncherTestUtils; // TEST시 사용

    @Autowired
    ApplicationContext ctx;

    @Autowired
    private SenderRepository repository;

    @Autowired
    @Qualifier(value = ChunkJobConfiguration.JOB_NAME)
    private Job job;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobRepository jobRepository;

    @BeforeEach
    private void init(){
        this.jobLauncherTestUtils = new JobLauncherTestUtils();
        this.jobLauncherTestUtils.setJobLauncher(jobLauncher);
        this.jobLauncherTestUtils.setJobRepository(jobRepository);
        this.jobLauncherTestUtils.setJob(job);
    }
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
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        //then
        Assertions.assertEquals(100,repository.findAll().size());
        Assertions.assertEquals(BatchStatus.COMPLETED,jobExecution.getStatus());

    }
}
