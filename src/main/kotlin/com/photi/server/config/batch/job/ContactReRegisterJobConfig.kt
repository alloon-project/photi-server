package com.photi.server.config.batch.job

import com.photi.server.domain.user.Contact
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
class ContactReRegisterJobConfig(

    @Value("\${spring.batch.chunk-size}")
    private val chunkSize: Int,
    private val entityManagerFactory: EntityManagerFactory,
    private val transactionManager: PlatformTransactionManager,
    private val jobRepository: JobRepository,
) {

    @Bean(CONTACT_RE_REGISTER_JOB_NAME)
    fun job(): Job {
        return JobBuilder(CONTACT_RE_REGISTER_JOB_NAME, jobRepository)
            .start(step())
            .build()
    }

    @Bean(BEAN_PREFIX + "step")
    @JobScope
    fun step(): Step {
        return StepBuilder(BEAN_PREFIX + "step", jobRepository)
            .chunk<Contact, Contact>(chunkSize, transactionManager)
            .reader(itemReader(null))
            .processor(itemProcessor())
            .writer(itemWriter())
            .build()
    }

    @Bean(BEAN_PREFIX + "itemReader")
    @StepScope
    fun itemReader(@Value("#{jobParameters[date]}") date: LocalDate?): JpaPagingItemReader<Contact> {
        val minusMonthDate = date?.minusMonths(MONTH_TO_SUBTRACT)
        return JpaPagingItemReaderBuilder<Contact>()
            .name(BEAN_PREFIX + "itemReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(chunkSize)
            .queryString(
                """
                SELECT c FROM Contact c 
                WHERE c.isDeleted = true 
                AND c.deletedDate <= :date 
                ORDER BY c.id ASC
                """.trimIndent()
            )
            .parameterValues(mapOf("date" to minusMonthDate))
            .build()
    }

    @Bean(BEAN_PREFIX + "itemProcessor")
    fun itemProcessor(): ItemProcessor<Contact, Contact> {
        return ItemProcessor {
            it.updateReRegisterStatus()
            it
        }
    }

    @Bean(BEAN_PREFIX + "itemWriter")
    fun itemWriter(): JpaItemWriter<Contact> {
        return JpaItemWriter<Contact>().apply {
            setEntityManagerFactory(entityManagerFactory)
        }
    }

    companion object {
        const val CONTACT_RE_REGISTER_JOB_NAME = "회원재가입가능상태"
        const val BEAN_PREFIX = CONTACT_RE_REGISTER_JOB_NAME + "_"
        const val MONTH_TO_SUBTRACT = 1L
    }
}
