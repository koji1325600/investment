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
import org.springframework.web.bind.annotation.RequestParam;

import com.example.investment.dao.BuyingDao;
import com.example.investment.dao.InvestmentDao;
import com.example.investment.dao.UserDao;
import com.example.investment.dao.InvestLogDao;
import com.example.investment.form.InvestmentForm;
import com.example.investment.repository.BuyingRepository;
import com.example.investment.repository.InvestLogRepository;
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
    BuyingRepository buyingRepository;

    @Autowired
    InvestLogRepository investLogRepository;

    @Autowired
    InvestmentService investmentService;

    /** 価格変動タスク */
    @Scheduled(initialDelay = 10000, fixedRate = 3000)
    public void priceFluctuation(){
        investmentService.fluctuation();
    }

    /** 自動取引タスク */
    @Scheduled(initialDelay = 11000, fixedRate = 3000)
    public void autoInvestment(){
        List<UserDao> userDaoList = userRepository.findAll();
        for (UserDao userDao: userDaoList) {
            if (userDao.getAuto() != null) {
                investmentService.autoInvestment(userDao);
            }
        }      
    }

    /** ホーム画面に遷移する */
    @GetMapping(path = "home")
    String home(Model model) {
        List<InvestmentDao> investList = investmentRepository.findByList();
        List<InvestLogDao> investLogDaoList = investLogRepository.findOrderByDateList();
        try {
            String userId = httpServletRequest.getSession().getAttribute("userId").toString();
            String userName = userRepository.findById(userId).get().getUserName();

            model.addAttribute("userName", userName);
            model.addAttribute("investList", investList);
            model.addAttribute("investLogDaoList", investLogDaoList);
            return "Invest/InvestHome";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    @GetMapping(path = "reHome")
    String reHome(@RequestParam String investName1, String investName2, String graphType, Model model){
        model.addAttribute("investName1", investName1);
        model.addAttribute("investName2", investName2);
        model.addAttribute("graphType", graphType);
        return home(model);
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

    /** 売買画面遷移 */
    @GetMapping(path = "buying")
    String buying(Model model) {
        List<InvestmentDao> investList = investmentRepository.findByList();
        String userId = httpServletRequest.getSession().getAttribute("userId").toString();
        List<BuyingDao> buyList = buyingRepository.findByUserIdList(userId);
    
        model.addAttribute("investList", investList);
        model.addAttribute("buyList", buyList);
        model.addAttribute("userDao", userRepository.findById(userId).get());
        return "Invest/buying";
    }

    /** 取引購入 */
    @PostMapping(path = "buyInvest")
    String buyInvest(@RequestParam String id, int quantity, Model model) {
        String userId = httpServletRequest.getSession().getAttribute("userId").toString();
        UserDao userDao = userRepository.findById(userId).get();
        investmentService.buying(userDao, id, quantity);
        return "redirect:buying";
    }

    /** 取引売却 */
    @PostMapping(path = "sellInvest")
    String sellInvest(@RequestParam String id, int quantity, Model model) {
        investmentService.selling(id, quantity);
        return "redirect:buying";
    }

    /** 取引詳細画面遷移 */
    @GetMapping(path = "detail")
    String investDetail(@RequestParam String id, Model model){
        try {
            InvestmentDao investDao = investmentRepository.findById(id).get();
            List<InvestLogDao> investLogDaoList = investLogRepository.findByInvestIdOrderByDateList(id);
            String userId = httpServletRequest.getSession().getAttribute("userId").toString();
            String userName = userRepository.findById(userId).get().getUserName();
            
            model.addAttribute("userName", userName);
            model.addAttribute("investDao", investDao);
            model.addAttribute("investLogDaoList", investLogDaoList);
            return "Invest/Detail";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    @PostMapping(path = "auto")
    String auto(@RequestParam String auto, Model model) {
        String userId = httpServletRequest.getSession().getAttribute("userId").toString();
        UserDao userDao = userRepository.findById(userId).get();
        if ("true".equals(auto)) {
            userDao.setAuto(true);
        } else {
            userDao.setAuto(null);
        }
        userRepository.save(userDao);
        return "redirect:buying";
    }
}
