package com.study.hello.spring.boot.controller;

import com.study.hello.spring.boot.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;



/**
 * @program: hello-spring-boot
 * @description: study 200416
 * @author: Zhouyuhang
 * @create: 2020-04-16 22:04
 */
@Controller
public class StudyController {

    @RequestMapping(value = "index", method = RequestMethod.GET)
    public String index(Model model){
        User user = new User();
        user.setName("小明");
        model.addAttribute("user", user);
        return "index";
    }
}
