package com.example.investment.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.investment.dao.BuyingDao;
import com.example.investment.dao.InvestLogDao;
import com.example.investment.dao.InvestmentDao;
import com.example.investment.dao.UserDao;
import com.example.investment.form.InvestmentForm;
import com.example.investment.repository.BuyingRepository;
import com.example.investment.repository.InvestLogRepository;
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

    @Autowired
    InvestLogRepository investLogRepository;

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
        int totalPrice = 0;

        for (InvestmentDao investDao: investList) {
            //商品名がアベレージだった場合は平均価格を設定し、
            //それ以外の場合は価格乱数処理を行う
            if ("アベレージ".equals(investDao.getName())) {
                investDao.setPrice(totalPrice / (investList.size() - 1));
                investmentRepository.save(investDao);
                addInvestLog(investDao);
                System.out.println("アベレージ:" + investDao.getPrice()); 
            } else {
                investmentRepository.save(randomPrice(investDao, investList.indexOf(investDao)));
                addInvestLog(investDao);
                totalPrice += investDao.getPrice();
            }
        }
        //取引ログの削除処理
        deleteInvestLog();
        System.out.println();
    }

    /** 取引価格変動ログ追加 */
    public void addInvestLog(InvestmentDao investDao) {
        //DateTimeFormatterクラスのオブジェクトを生成する
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        //現在日時を取得し、フォーマット変換する
        LocalDateTime nowDefaltDate = LocalDateTime.now();
        LocalDateTime nowDateTime = LocalDateTime.parse(nowDefaltDate.format(dtf), dtf);
        InvestLogDao investLogDao = new InvestLogDao();

        investLogDao.setInvestId(investDao.getId());
        investLogDao.setInvestName(investDao.getName());
        investLogDao.setPrice(investDao.getPrice());
        investLogDao.setDate(nowDateTime);
        investLogDao.addTodoDao();

        investLogRepository.save(investLogDao);
    }

    /** 容量を超えたログを削除する。 */
    public void deleteInvestLog() {
        List<InvestLogDao> investLogDaoList = investLogRepository.findOrderByDateDescList();
        List<InvestmentDao> investmentDaoList = investmentRepository.findAll();
        int logSize = investLogDaoList.size();
        int investSize = investmentDaoList.size();
        //取引ログが300以上の場合は削除処理を行う
        if (logSize > 300) {
            //取引ログが取引数以上300を超えていたら、その分多く削除する。
            if (logSize > 300 + investSize) {
                investSize = investSize * 2;
            }
            for (int i = logSize - 1; i >= logSize - investSize; i--) {
                investLogRepository.delete(investLogDaoList.get(i));
            }
        }
    }

    /** 価格乱数処理 */
    public InvestmentDao randomPrice(InvestmentDao investDao, int index) {
        Random rand = new Random();
        int maxPrice = investDao.getMaxPrice();
        int minPrice = investDao.getMinPrice();
        int rangePrice = (maxPrice - minPrice) / 2;
        int condition = rangePrice * condition(investDao.getCondit()) / 100;
        int minRangePrice = rangePrice / 2;
        int price = investDao.getPrice();
        int change = 0;

        //価格が0だった場合は最小価格を基準に価格乱数を生成する
        if (price == 0) {
            price = rand.nextInt(rangePrice) + (minPrice + minRangePrice + condition);
        } else {
            price = rand.nextInt(rangePrice) + (price - minRangePrice + condition);
        }

        System.out.print(investDao.getName() + " " + investDao.getCondit() + " rand:" + price);
        //暴落時
        if (price < investDao.getCrash()) {
            System.out.print(" 暴落");
            price = rand.nextInt(rangePrice) + (minPrice + minRangePrice);
            investDao.setMaxPrice(maxPrice - minRangePrice);
            investDao.setMinPrice(minPrice - minRangePrice / 2);
            investDao.setCrash(investDao.getCrash() - minRangePrice / 2);
            change -= minRangePrice;
            investDao.setCondit("普通");
            List<BuyingDao> buyingList = buyingRepository.findByInvestIdList(investDao.getId());

            //暴落した商品を売却する
            for (BuyingDao buyingDao: buyingList) {
                UserDao userDao = userRepository.findById(buyingDao.getUserId()).get();
                int money = buyingDao.getQuantity() * investDao.getCrash();
                userDao.setMoney(userDao.getMoney() + money);
                userRepository.save(userDao);
                buyingRepository.delete(buyingDao);
            }
        }

        // 価格が最大価格より上の場合、ペナルティ
        if (price - maxPrice > minRangePrice) {
            investDao.setCondit("不調");
            investDao.setMaxPrice(maxPrice + minRangePrice);
            investDao.setMinPrice(minPrice + minRangePrice / 2);
            investDao.setCrash(investDao.getCrash() + minRangePrice / 2);
            change += minRangePrice;
        } else {
            updateCondition(investDao);
        }
        investDao.setPrice(price);
        System.out.print(" set:" + price + " " + investDao.getCondit() + " " + change + "     ");
        if (index % 2 == 1) {
            System.out.println();
        }
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
        } else if (random < 7) {
            condit = "好調";
        } else if (random < 12) {
            condit = "普通";
        } else if (random < 14) {
            condit = "不調";
        } else if (random < 15) {
            condit = "絶不調";
        }

        investDao.setCondit(condit);
    }

    /** 購入処理 */
    public void buying(UserDao userDao, String id, int quantity) {
        InvestmentDao investDao = investmentRepository.findById(id).get();
        String userId = userDao.getUserId();
        BuyingDao buyingDao = buyingRepository.findByInvestIdAndUserIdDao(id, userId);

        //購入済みの商品の場合は購入数を足す
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

        int totalPrice = investDao.getPrice() * quantity;
        int money = userDao.getMoney();
        if (money < totalPrice) {
            return;
        }
        buyingRepository.save(buyingDao);

        //所持金から合計購入価格を差し引く
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
            //売却個数が購入個数より多い場合は、処理を中断
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

        //所持金に合計売却価格を足す
        int money = quantity * investDao.getPrice();
        userDao.setMoney(userDao.getMoney() + money);
        userRepository.save(userDao);
    }

    /** 自動取引処理 */
    public void autoInvestment(UserDao userDao) {
        List<InvestmentDao> investDaoList = investmentRepository.findAll();
        int count;
        
        for (InvestmentDao investDao: investDaoList) {
            if ("絶不調".equals(investDao.getCondit()) || "不調".equals(investDao.getCondit()) || "アベレージ".equals(investDao.getName())) {
                continue;
            }
            count = buyCheck(investDao, userDao.getMoney());
            if (count > 0) {
                buying(userDao, investDao.getId(), count);
            }
        }
    }

    /** 自動購入チェック */
    public int buyCheck(InvestmentDao investDao, int money) {
        int count = 0;
        int maxPrice = investDao.getMaxPrice();
        int minPrice = investDao.getMinPrice();
        int rangePrice = (maxPrice - minPrice) / 2;
        int minRangePrice = rangePrice / 2;
        int price = investDao.getPrice();
        String condit = investDao.getCondit();

        if (price < minPrice + rangePrice) {
            count++;
        }
        if (minPrice + minRangePrice < price && price < minPrice + rangePrice) {
            count++;
        }
        if ("好調".equals(condit)) {
            count++;
        }
        if ("絶好調".equals(condit)) {
            count += 2;
        }
        if (count > 0 && money > price * 10) {
            count++;
            if (money > price * 20) {
                count++;
            }
        }
        return count;
    }
}
