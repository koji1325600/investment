package com.example.investment.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.investment.dao.InvestmentDao;
import com.example.investment.repository.InvestmentRepository;
import com.example.investment.repository.UserRepository;

@Controller
@RequestMapping("home")
public class InvestmentController {
    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    UserRepository userRepository;

    @Autowired
    InvestmentRepository investmentRepository;

    /** ホーム画面に遷移する */
    @GetMapping
    String todo(Model model) {
        List<InvestmentDao> investList = investmentRepository.findByList();
        model.addAttribute("investList", investList);
        return "Invest/InvestHome";
    }
}
