package com.example.investment.service;

import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.investment.dao.BuyingDao;
import com.example.investment.dao.InvestmentDao;
import com.example.investment.dao.UserDao;
import com.example.investment.form.InvestmentForm;
import com.example.investment.repository.BuyingRepository;
import com.example.investment.repository.InvestmentRepository;
import com.example.investment.repository.UserRepository;

@Service
public class InvestmentService {
    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    InvestmentRepository investmentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BuyingRepository buyingRepository;

    /** 取引追加処理 */
    public void addInvent(InvestmentForm investmentForm) {
        InvestmentDao investmentDao = new InvestmentDao();
        BeanUtils.copyProperties(investmentForm, investmentDao);
        investmentDao.setCondit("普通");
        investmentDao.addTodoDao();
        investmentRepository.save(investmentDao);
    }

    /** 価格変動処理 */
    public void fluctuation() {
        List<InvestmentDao> investList = investmentRepository.findByList();

        for (InvestmentDao investDao: investList) {
            investmentRepository.save(randomPrice(investDao));
        }
        System.out.println();
    }

    /** 価格乱数処理 */
    public InvestmentDao randomPrice(InvestmentDao investDao) {
        Random rand = new Random();
        int maxPrice = investDao.getMaxPrice();
        int minPrice = investDao.getMinPrice();
        int rangePrice = (maxPrice - minPrice) / 2;
        int condition = rangePrice * condition(investDao.getCondit()) / 100;
        int minRangePrice = rangePrice / 2;
        int price = investDao.getPrice();

        if (price == 0) {
            price = rand.nextInt(rangePrice) + (minPrice + minRangePrice + condition);
        } else {
            price = rand.nextInt(rangePrice) + (price - minRangePrice + condition);
        }

        System.out.print(investDao.getName() + " " + investDao.getCondit() + " rand:" + price);
        if (price < investDao.getCrash()) {
            System.out.print(" 暴落");
            price = rand.nextInt(rangePrice) + (minPrice + minRangePrice);
            List<BuyingDao> buyingList = buyingRepository.findByInvestIdList(investDao.getId());

            for (BuyingDao buyingDao: buyingList) {
                UserDao userDao = userRepository.findById(buyingDao.getUserId()).get();
                int money = buyingDao.getQuantity() * investDao.getCrash();
                userDao.setMoney(userDao.getMoney() + money);
                userRepository.save(userDao);
                buyingRepository.delete(buyingDao);
            }
        }

        // 価格が最大価格より上にならないように
        if (price > maxPrice) {
            price = maxPrice;
            List<BuyingDao> buyingList = buyingRepository.findByInvestIdList(investDao.getId());

            for (BuyingDao buyingDao: buyingList) {
                UserDao userDao = userRepository.findById(buyingDao.getUserId()).get();
                int money = buyingDao.getQuantity() * investDao.getMaxPrice();
                userDao.setMoney(userDao.getMoney() + money);
                userRepository.save(userDao);
                buyingRepository.delete(buyingDao);
            }
        }
        investDao.setPrice(price);
        updateCondition(investDao);
        System.out.println(" set:" + price + " " + investDao.getCondit());
        return investDao;
    }

    /** 調子判定処理 */
    public int condition(String condit) {
        int conditNum;
        switch(condit) {
            case "絶好調": conditNum = 20; break;
            case "好調": conditNum = 10; break;
            case "普通": conditNum = 0; break;
            case "絶不調": conditNum = -10; break;
            case "不調": conditNum = -20; break;
            default: conditNum = 0; break;
        }
        return conditNum;
    }

    /** 調子設定処理 */
    public void updateCondition(InvestmentDao investDao) {
        Random rand = new Random();
        int random = rand.nextInt(40) + 1;
        String condit = investDao.getCondit();

        if (random < 2) {
            condit = "絶好調";
        } else if (random < 5) {
            condit = "好調";
        } else if (random < 11) {
            condit = "普通";
        } else if (random < 14) {
            condit = "不調";
        } else if (random < 15) {
            condit = "絶不調";
        }

        investDao.setCondit(condit);
    }

    /** 購入処理 */
    public void buying(String id, int quantity) {
        InvestmentDao investDao = investmentRepository.findById(id).get();
        String userId = httpServletRequest.getSession().getAttribute("userId").toString();
        UserDao userDao = userRepository.findById(userId).get();
        BuyingDao buyingDao = buyingRepository.findByInvestIdAndUserIdDao(id, userId);

        if (buyingDao == null) {
            buyingDao = new BuyingDao();
            buyingDao.addTodoDao();
            buyingDao.setInvestId(id);
            buyingDao.setInvestName(investDao.getName());
            buyingDao.setUserId(userId);
            buyingDao.setUserName(userDao.getUserName());
            buyingDao.setQuantity(quantity);
            buyingDao.setBuyPrice(investDao.getPrice());
        } else {
            quantity = quantity + buyingDao.getQuantity();
            buyingDao.setQuantity(quantity);
            buyingDao.setBuyPrice(investDao.getPrice());
        }

        buyingRepository.save(buyingDao);

        int totalPrice = investDao.getPrice() * quantity;
        int money = userDao.getMoney();

        userDao.setMoney(money - totalPrice);
        userRepository.save(userDao);
    }

    /** 売却処理 */
    public void selling(String id, int quantity) {
        InvestmentDao investDao = investmentRepository.findById(id).get();
        String userId = httpServletRequest.getSession().getAttribute("userId").toString();
        UserDao userDao = userRepository.findById(userId).get();
        BuyingDao buyingDao = buyingRepository.findByInvestIdAndUserIdDao(id, userId);

        try {
            if (quantity > buyingDao.getQuantity()) {
                return;
            } else if (quantity == buyingDao.getQuantity()) {
                buyingRepository.delete(buyingDao);
            } else {
                buyingDao.setQuantity(buyingDao.getQuantity() - quantity);
                buyingRepository.save(buyingDao);
            }
        } catch (Exception e) {
            System.out.println("例外発生!!");
            int money = quantity * investDao.getPrice();
            userDao.setMoney(userDao.getMoney() + money);
            userRepository.save(userDao);
        }

        int money = quantity * investDao.getPrice();
        userDao.setMoney(userDao.getMoney() + money);
        userRepository.save(userDao);
    }
}
