package com.alloon.alloonserver.domain.mission

import com.alloon.alloonserver.domain.base.BasePermanentEntity
import jakarta.persistence.*

@Entity
class MissionRule(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_rule_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    val mission: Mission,

    @Column(nullable = false, length = 30)
    val rule: String,
) : BasePermanentEntity() {
}