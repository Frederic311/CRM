package org.crm.student.application_management_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PipelineController {

    @GetMapping("/pipeline")
    public String showPipelinePage() {
        return "pipeline";
    }
}
