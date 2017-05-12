package com.raowei.start;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 */
@Controller
@EnableAutoConfiguration
@ComponentScan(value = "com.raowei.start")
public class SimpleController {
    @Autowired
    private  MakeLoanService makeLoanService;
    /**
     *
     */
    @RequestMapping("/")
    @ResponseBody
    public String hello () {
        return "HelloWord";
    }

    @RequestMapping("/makeloan")
    @ResponseBody
    public String makeLoan() {
        makeLoanService.doMakLoan("sdlkfjsd");
        return "放款成功";
    }
    public static void main(String[] args) {
        SpringApplication.run(SimpleController.class,args);
    }
}
