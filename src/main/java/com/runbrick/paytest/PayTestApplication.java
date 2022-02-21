package com.runbrick.paytest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@Controller
public class PayTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayTestApplication.class, args);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

}
