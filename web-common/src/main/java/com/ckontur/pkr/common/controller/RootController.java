package com.ckontur.pkr.common.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

public interface RootController {

    @GetMapping("/")
    ModelAndView root();

}
