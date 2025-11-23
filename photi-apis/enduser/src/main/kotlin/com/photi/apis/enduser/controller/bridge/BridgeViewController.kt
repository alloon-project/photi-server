package com.photi.apis.enduser.controller.bridge

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class BridgeViewController {

    @GetMapping("/open")
    fun openApp(model: Model): String {
        model.addAttribute("deepLink", "https://www.photi.store/open")
        return "bridge"
    }
}
