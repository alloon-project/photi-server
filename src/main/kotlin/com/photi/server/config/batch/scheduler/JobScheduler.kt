package com.photi.server.config.batch.scheduler

import com.photi.server.config.batch.job.ChallengeStatusEndJobConfig.Companion.CHALLENGE_END_JOB_NAME
import com.photi.server.config.batch.job.ContactReRegisterJobConfig.Companion.CONTACT_RE_REGISTER_JOB_NAME
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.JobParametersInvalidException
import org.springframework.batch.core.configuration.JobRegistry
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.launch.NoSuchJobException
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException
import org.springframework.batch.core.repository.JobRestartException
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class JobScheduler(
    private val jobLauncher: JobLauncher,
    private val jobRegistry: JobRegistry,
) {

    @Scheduled(cron = "0 * 3 * * *")
    fun runChallengeEndJob() {
        val date = LocalDate.now().toString()
        try {
            val job = jobRegistry.getJob(CHALLENGE_END_JOB_NAME)
            val jobParameters = JobParametersBuilder()
                .addString(JOB_PARAMETER, date)
                .toJobParameters()
            jobLauncher.run(job, jobParameters)
        } catch (e: NoSuchJobException) {
            throw RuntimeException(e)
        } catch (e: JobInstanceAlreadyCompleteException) {
            throw RuntimeException(e)
        } catch (e: JobExecutionAlreadyRunningException) {
            throw RuntimeException(e)
        } catch (e: JobParametersInvalidException) {
            throw RuntimeException(e)
        } catch (e: JobRestartException) {
            throw RuntimeException(e)
        }
    }

    @Scheduled(cron = "0 * 4 * * *")
    fun runContactReRegisterJob() {
        val date = LocalDate.now().toString()
        try {
            val job = jobRegistry.getJob(CONTACT_RE_REGISTER_JOB_NAME)
            val jobParameters = JobParametersBuilder()
                .addString(JOB_PARAMETER, date)
                .toJobParameters()
            jobLauncher.run(job, jobParameters)
        } catch (e: NoSuchJobException) {
            throw RuntimeException(e)
        } catch (e: JobInstanceAlreadyCompleteException) {
            throw RuntimeException(e)
        } catch (e: JobExecutionAlreadyRunningException) {
            throw RuntimeException(e)
        } catch (e: JobParametersInvalidException) {
            throw RuntimeException(e)
        } catch (e: JobRestartException) {
            throw RuntimeException(e)
        }
    }

    companion object {
        const val JOB_PARAMETER = "date"
    }
}
