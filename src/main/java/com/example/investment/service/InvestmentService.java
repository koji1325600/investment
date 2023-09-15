package com.example.investment.service;

import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.investment.dao.InvestmentDao;
import com.example.investment.form.InvestmentForm;
import com.example.investment.repository.InvestmentRepository;

@Service
public class InvestmentService {
    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    InvestmentRepository investmentRepository;

    /** 取引追加処理 */
    public void addInvent(InvestmentForm investmentForm) {
        InvestmentDao investmentDao = new InvestmentDao();
        BeanUtils.copyProperties(investmentForm, investmentDao);
        investmentDao.addTodoDao();
        investmentRepository.save(investmentDao);
    }

    /** 価格変動処理 */
    public void fluctuation() {
        List<InvestmentDao> investList = investmentRepository.findByList();

        for (InvestmentDao investDao: investList) {
            investmentRepository.save(randomPrice(investDao));
        }
    }

    /** 価格乱数処理 */
    public InvestmentDao randomPrice(InvestmentDao investDao) {
        Random rand = new Random();
        int maxPrice = investDao.getMaxPrice();
        int minPrice = investDao.getMinPrice();
        int rangePrice = (maxPrice - minPrice) / 2;
        int minRangePrice = rangePrice / 2;
        int price = investDao.getPrice();

        if (price == 0) {
            price = rand.nextInt(rangePrice) + (minPrice + minRangePrice);
        } else {
            price = rand.nextInt(rangePrice) + (price - minRangePrice);
        }

        System.out.println("rand:" + price);
        if (price < investDao.getCrash()) {
            System.out.println("暴落");
            price = rand.nextInt(rangePrice) + (minPrice + minRangePrice);
        }

        // 価格が最大価格より上にならないように
        if (price > maxPrice) {
            price = maxPrice;
        }
        System.out.println("set:" + price);
        investDao.setPrice(price);
        return investDao;
    }
}
