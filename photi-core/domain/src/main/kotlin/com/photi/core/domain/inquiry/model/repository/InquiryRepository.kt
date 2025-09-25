package com.photi.core.domain.inquiry.model.repository

import com.photi.core.domain.inquiry.model.Inquiry
import org.springframework.data.jpa.repository.JpaRepository

interface InquiryRepository : JpaRepository<Inquiry, Long>
