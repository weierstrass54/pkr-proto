package com.ckontur.pkr.exam.controller;

import com.ckontur.pkr.common.controller.RootController;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@Controller
public class SwaggerController implements RootController {
    @Override
    public ModelAndView root() {
        return new ModelAndView("redirect:/swagger-ui/");
    }
}
