package com.alloon.alloonserver.domain.report

import com.alloon.alloonserver.common.util.PasswordUtility
import com.alloon.alloonserver.domain.base.ServiceStatus
import com.alloon.alloonserver.domain.report.ReportCategoryType.*
import com.alloon.alloonserver.domain.user.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Stream

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ReportCategoryRepositoryTest(
    @Autowired private val reportCategoryRepository: ReportCategoryRepository,
    @Autowired private val userRoleRepository: UserRoleRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val contactRepository: ContactRepository,
    @Autowired private val passwordUtility: PasswordUtility,
) {

    @ParameterizedTest(name = "[{index}] {0} 종류의 신고 항목 설명 전체 조회가 정상 작동한다")
    @EnumSource(ReportCategoryType::class)
    @DisplayName("신고 항목 설명 전체 조회가 정상 작동한다")
    fun givenValid_whenFindAll_thenReturn(type: ReportCategoryType) {
        // given
        val adminRole = createAndSaveAdminWithContact()

        val reportCategories: List<ReportCategory> = listOf(
            createAndSaveReportCategory(adminRole.user, type, "항목1", 1),
            createAndSaveReportCategory(adminRole.user, type, "항목2", 2),
            createAndSaveReportCategory(adminRole.user, type, "항목3", 3),
            createAndSaveReportCategory(adminRole.user, type, "항목4", 4),
            createAndSaveReportCategory(adminRole.user, type, "항목5", 5),
        )
        reportCategories[3].serviceStatus = ServiceStatus.ADMIN_DEL
        reportCategories[4].serviceStatus = ServiceStatus.ADMIN_DEL
        reportCategoryRepository.saveAll(listOf(reportCategories[3], reportCategories[4]))

        // when
        val result = reportCategoryRepository.findAllDescription(type)

        // then
        assertThat(result)
            .containsExactly(
                reportCategories[0].description,
                reportCategories[1].description,
                reportCategories[2].description
            )
    }

    @ParameterizedTest(name = "[{index}] ID와 신고 종류 {1}인 신고 항목 존재 여부 조회시 {3}을 반환된다")
    @MethodSource("providerExists")
    @DisplayName("신고 항목 존재 여부 조회가 정상 작동한다")
    fun givenProvider_whenExists_thenReturn(type: ReportCategoryType, expected: Boolean) {
        // given
        val adminRole = createAndSaveAdminWithContact()

        val reportCategory = createAndSaveReportCategory(adminRole.user, type, "항목", 1)
        val id = if (expected) reportCategory.id!! else reportCategory.id!!.inc()

        // when
        val result = reportCategoryRepository.find(id, type)

        // then
        if (expected) assertThat(result).isEqualTo(reportCategory)
        else assertThat(result).isNull()
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
        val user = userRepository.save(
            User(
                contact = contact,
                username = "tester",
                password = encryptedPassword,
                imageUrl = ""
            )
        )

        return userRoleRepository.save(UserRole(user = user, role = Role.ADMIN))
    }

    companion object {
        @JvmStatic
        private fun providerExists(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(MISSION, true),
                Arguments.of(MISSION, false),
                Arguments.of(MISSION_MEMBER, true),
                Arguments.of(MISSION_MEMBER, false),
                Arguments.of(FEED, true),
                Arguments.of(FEED, false),
            )
        }
    }
}