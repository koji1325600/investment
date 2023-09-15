package com.example.investment.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.investment.form.UserFrom;
import com.example.investment.repository.UserRepository;
import com.example.investment.service.UserService;

@Controller
@RequestMapping("users")
public class UserController {
    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    /** ユーザ作成画面遷移 */
    @GetMapping
    String user() {
        return "users/CreateUser";
    }

    /** ユーザ作成 */
    @PostMapping(path = "create")
    String create(@Validated UserFrom userFrom, Model model) {
        Boolean isCreate = userService.create(userFrom);
        if (isCreate) {
            return "redirect:/login";
        } else {
            model.addAttribute("error", "そのメールアドレスは使用出来ません");
            return "users/CreateUser";
        }
    }
}
