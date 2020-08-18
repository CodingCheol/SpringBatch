package com.springbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;


@TestConfiguration
@EnableBatchProcessing
//@ComponentScan(basePackages = "com.springbatch")
//@EnableJpaRepositories(basePackages = "com.springbatch.jpa.repository")
@EnableAutoConfiguration  //테스트 시작 시 설정한 config 를 읽어서 해당 Bean 만을 등록하기 위하여 사용
public class TestBatchLegacyConfig {
}
