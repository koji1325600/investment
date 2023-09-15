package com.example.investment.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.investment.dao.InvestmentDao;
import com.example.investment.form.InvestmentForm;
import com.example.investment.repository.InvestmentRepository;
import com.example.investment.repository.UserRepository;
import com.example.investment.service.InvestmentService;

@Controller
@RequestMapping("invest")
public class InvestmentController {
    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    UserRepository userRepository;

    @Autowired
    InvestmentRepository investmentRepository;

    @Autowired
    InvestmentService investmentService;

    /** 価格変動タスク */
    @Scheduled(fixedRate = 5000)
    public void priceFluctuation(){
        investmentService.fluctuation();
    }

    /** ホーム画面に遷移する */
    @GetMapping(path = "home")
    String todo(Model model) {
        List<InvestmentDao> investList = investmentRepository.findByList();
        model.addAttribute("investList", investList);
        return "Invest/InvestHome";
    }

    /** 取引新規作成画面遷移 */
    @GetMapping(path = "create")
    String create(Model model) {
        return "Invest/CreateInvest";
    }

    /** 取引新規作成 */
    @PostMapping(path = "addInvest")
    String addInvest(@Validated InvestmentForm investmentForm, Model model) {
        investmentService.addInvent(investmentForm);
        return "redirect:create";
    }
}
