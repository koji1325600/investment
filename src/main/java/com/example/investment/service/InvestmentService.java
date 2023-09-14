package com.example.investment.service;

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
    public void addInvent(InvestmentForm investmentForm){

        InvestmentDao investmentDao = new InvestmentDao();
        BeanUtils.copyProperties(investmentForm, investmentDao);
        investmentDao.addTodoDao();
        investmentRepository.save(investmentDao);
    }
}
