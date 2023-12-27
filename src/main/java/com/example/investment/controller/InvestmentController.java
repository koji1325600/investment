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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.investment.dto.AssetsDto;
import com.example.investment.dto.BuyingDto;
import com.example.investment.dto.InvestId;
import com.example.investment.dto.InvestLogDto;
import com.example.investment.dto.InvestmentDto;
import com.example.investment.dto.UserDto;
import com.example.investment.form.InvestmentForm;
import com.example.investment.repository.AssetsRepository;
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

    @Autowired
    AssetsRepository assetsRepository;

    /** 価格変動タスク */
    @Scheduled(initialDelay = 10000, fixedRate = 3000)
    public void priceFluctuation(){
        investmentService.fluctuation();
    }

    /** 自動取引タスク */
    @Scheduled(initialDelay = 11000, fixedRate = 3000)
    public void autoInvestment(){
        List<UserDto> userDtoList = userRepository.findAll();
        for (UserDto userDto: userDtoList) {
            if (userDto.getAuto() != null) {
                investmentService.autoInvestment(userDto);
            }
        }      
    }

    /** ホーム画面に遷移する */
    @GetMapping(path = "home")
    String home(Model model) {
        List<InvestmentDto> investList = investmentRepository.findByList();
        List<InvestLogDto> investLogDtoList = investLogRepository.findOrderByDateList();
        try {
            String userId = httpServletRequest.getSession().getAttribute("userId").toString();
            String userName = userRepository.findById(userId).get().getUserName();

            model.addAttribute("userName", userName);
            model.addAttribute("investList", investList);
            model.addAttribute("investLogDtoList", investLogDtoList);
            return "Invest/InvestHome";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    /** ホーム画面　折れ線グラフ更新処理 */
    @PostMapping(path = "homeLineGraphAjax")
    @ResponseBody
    List<InvestLogDto> homeLineGraphAjax(){
        return investLogRepository.findOrderByDateList();
    }

    /** ホーム画面　円グラフ更新処理 */
    @PostMapping(path = "homePieGraphAjax")
    @ResponseBody
    List<InvestmentDto> homePieGraphAjax(){
        return investmentRepository.findByList();
    }

    /** ホーム画面　テーブル更新処理 */
    @PostMapping(path = "homeTableAjax")
    @ResponseBody
    List<InvestmentDto> homeTableAjax(){
        return investmentRepository.findByList();
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
        try {
            List<InvestmentDto> investList = investmentRepository.findByList();
            String userId = httpServletRequest.getSession().getAttribute("userId").toString();
            List<BuyingDto> buyList = buyingRepository.findByUserIdList(userId);
            List<AssetsDto> assetsDtoList = assetsRepository.findByUserIdOrderByDateList(userId);
        
            model.addAttribute("investList", investList);
            model.addAttribute("buyList", buyList);
            model.addAttribute("assetsDtoList", assetsDtoList);
            model.addAttribute("userDto", userRepository.findById(userId).get());
            return "Invest/buying";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    /** 売買画面 グラフ更新処理 */
    @PostMapping(path = "buyingAjax")
    @ResponseBody
    List<AssetsDto> buyingAjax(){
        String userId = httpServletRequest.getSession().getAttribute("userId").toString();
        return assetsRepository.findByUserIdOrderByDateList(userId);
    }

    /** 売買画面　テーブル更新処理 */
    @PostMapping(path = "buyingTableAjax")
    @ResponseBody
    List<BuyingDto> buyingTableAjax(){
        String userId = httpServletRequest.getSession().getAttribute("userId").toString();
        return buyingRepository.findByUserIdList(userId);
    }

    /** 売買画面　所持金更新処理 */
    @PostMapping(path = "buyingUserAjax")
    @ResponseBody
    UserDto buyingUserAjax(){
        String userId = httpServletRequest.getSession().getAttribute("userId").toString();
        return userRepository.findById(userId).get();
    }

    /** 取引購入 */
    @PostMapping(path = "buyInvest")
    String buyInvest(@RequestParam String id, int quantity, Model model) {
        String userId = httpServletRequest.getSession().getAttribute("userId").toString();
        UserDto userDto = userRepository.findById(userId).get();
        investmentService.buying(userDto, id, quantity);
        return "redirect:buying";
    }

    /** 取引売却 */
    @PostMapping(path = "sellInvest")
    String sellInvest(@RequestParam String id, int quantity, Model model) {
        String userId = httpServletRequest.getSession().getAttribute("userId").toString();
        UserDto userDto = userRepository.findById(userId).get();
        investmentService.selling(userDto, id, quantity);
        return "redirect:buying";
    }

    /** 取引詳細画面遷移 */
    @GetMapping(path = "detail")
    String investDetail(@RequestParam String id, Model model){
        try {
            InvestmentDto investDto = investmentRepository.findById(id).get();
            List<InvestLogDto> investLogDtoList = investLogRepository.findByInvestIdOrderByDateList(id);
            String userId = httpServletRequest.getSession().getAttribute("userId").toString();
            String userName = userRepository.findById(userId).get().getUserName();
            
            model.addAttribute("userName", userName);
            model.addAttribute("investDto", investDto);
            model.addAttribute("investLogDtoList", investLogDtoList);
            return "Invest/Detail";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    /** 取引詳細画面 グラフ更新処理 */
    @PostMapping(path = "detailGraphAjax")
    @ResponseBody
    List<InvestLogDto> detailGraphAjax(@RequestBody InvestId investId){
        return investLogRepository.findByInvestIdOrderByDateList(investId.getInvestId());
    }

    /** 取引詳細画面 テーブル更新処理 */
    @PostMapping(path = "detailTableAjax")
    @ResponseBody
    InvestmentDto detailTableAjax(@RequestBody InvestId investId){
        return investmentRepository.findById(investId.getInvestId()).get();
    }

    /** 自動取引切り替え */
    @PostMapping(path = "auto")
    String auto(@RequestParam String auto, Model model) {
        String userId = httpServletRequest.getSession().getAttribute("userId").toString();
        UserDto userDto = userRepository.findById(userId).get();
        if ("true".equals(auto)) {
            userDto.setAuto(true);
        } else {
            userDto.setAuto(null);
        }
        userRepository.save(userDto);
        return "redirect:buying";
    }
}
