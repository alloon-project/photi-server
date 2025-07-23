package com.photi.server.config.batch.job

import com.photi.server.domain.challenge.Challenge
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import java.time.LocalDate

@Configuration
class ChallengeStatusEndJobConfig(

    @Value("\${spring.batch.chunk-size}")
    private val chunkSize: Int,
    private val entityManagerFactory: EntityManagerFactory,
    private val transactionManager: PlatformTransactionManager,
    private val jobRepository: JobRepository,
) {

    @Bean(CHALLENGE_END_JOB_NAME)
    fun job(): Job {
        return JobBuilder(CHALLENGE_END_JOB_NAME, jobRepository)
            .start(step())
            .build()
    }

    @Bean(BEAN_PREFIX + "step")
    @JobScope
    fun step(): Step {
        return StepBuilder(BEAN_PREFIX + "step", jobRepository)
            .chunk<Challenge, Challenge>(chunkSize, transactionManager)
            .reader(itemReader(null))
            .processor(itemProcessor())
            .writer(itemWriter())
            .build()
    }

    @Bean(BEAN_PREFIX + "itemReader")
    @StepScope
    fun itemReader(@Value("#{jobParameters[date]}") date: LocalDate?): JpaPagingItemReader<Challenge> {
        return JpaPagingItemReaderBuilder<Challenge>()
            .name(BEAN_PREFIX + "itemReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(chunkSize)
            .queryString(
                """
                SELECT c FROM Challenge c 
                WHERE c.serviceStatus = 'ACTIVE' 
                AND c.endDate < :date 
                ORDER BY c.id ASC
                """.trimIndent()
            )
            .parameterValues(mapOf("date" to date))
            .build()
    }

    @Bean(BEAN_PREFIX + "itemProcessor")
    fun itemProcessor(): ItemProcessor<Challenge, Challenge> {
        return ItemProcessor {
            it.updateChallengeStatusEnd()
            it
        }
    }

    @Bean(BEAN_PREFIX + "itemWriter")
    fun itemWriter(): JpaItemWriter<Challenge> {
        return JpaItemWriter<Challenge>().apply {
            setEntityManagerFactory(entityManagerFactory)
        }
    }

    companion object {
        const val CHALLENGE_END_JOB_NAME = "챌린지종료상태"
        const val BEAN_PREFIX = CHALLENGE_END_JOB_NAME + "_"
    }
}
