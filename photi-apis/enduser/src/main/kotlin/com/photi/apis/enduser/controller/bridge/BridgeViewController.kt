package com.photi.apis.enduser.controller.bridge

import io.swagger.v3.oas.annotations.Parameter
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class BridgeViewController {

    @GetMapping("/open")
    fun openApp(
        model: Model,
        @RequestParam("challenge_id") @Parameter(description = "챌린지 id") challengeId: Long,
    ): String {
        model.addAttribute("challengeId", challengeId)
        return "bridge"
    }
}
