package com.alloon.alloonserver.api.service.report

import com.alloon.alloonserver.common.util.PasswordUtility
import com.alloon.alloonserver.domain.report.ReportCategory
import com.alloon.alloonserver.domain.report.ReportCategoryRepository
import com.alloon.alloonserver.domain.report.ReportCategoryType
import com.alloon.alloonserver.domain.user.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ReportServiceTest(
    @Autowired private val reportService: ReportService,
    @Autowired private val reportCategoryRepository: ReportCategoryRepository,
    @Autowired private val userRoleRepository: UserRoleRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val contactRepository: ContactRepository,
    @Autowired private val passwordUtility: PasswordUtility,
) {

    @ParameterizedTest(name = "[{index}] {0} 종류의 신고 항목 설명 전체 조회가 정상 작동한다")
    @EnumSource(ReportCategoryType::class)
    @DisplayName("신고 항목 설명 전체 조회가 정상 작동한다")
    fun givenEnum_whenGetAllReportCategoryDescription_thenReturn(type: ReportCategoryType) {
        // given
        val adminRole = createAndSaveAdminWithContact()
        val reportCategory = createAndSaveReportCategory(adminRole.user, type, "항목", 1)

        // when
        val result = reportService.getAllReportCategoryDescription(type)

        // then
        assertThat(result).containsExactly(reportCategory.description)
    }

    private fun createAndSaveReportCategory(
        admin: User,
        type: ReportCategoryType,
        description: String,
        sort: Int,
    ): ReportCategory {
        return reportCategoryRepository.save(
            ReportCategory(
            admin = admin,
            type = type,
            description = description,
            sort = sort
        )
        )
    }

    private fun createAndSaveAdminWithContact(): UserRole {
        val contact = contactRepository.save(
            Contact(
                email = "tester@alloon.com",
                verificationCode = "000000",
                verifyYn = true
            )
        )

        val encryptedPassword = passwordUtility.encryptPassword("password1!")
        val user = userRepository.save(User(contact = contact, username = "tester", password = encryptedPassword, imageUrl = ""))

        return userRoleRepository.save(UserRole(user = user, role = Role.ADMIN))
    }
}