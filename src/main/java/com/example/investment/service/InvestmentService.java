package com.example.investment.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.investment.dto.BuyingDto;
import com.example.investment.dto.InvestLogDto;
import com.example.investment.dto.InvestmentDto;
import com.example.investment.dto.SellingDto;
import com.example.investment.dto.UserDto;
import com.example.investment.form.InvestmentForm;
import com.example.investment.repository.BuyingRepository;
import com.example.investment.repository.InvestLogRepository;
import com.example.investment.repository.InvestmentRepository;
import com.example.investment.repository.SellingRepository;
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

    @Autowired
    SellingRepository sellingRepository;

    /** 取引追加処理 */
    public void addInvent(InvestmentForm investmentForm) {
        InvestmentDto investmentDto = new InvestmentDto();
        BeanUtils.copyProperties(investmentForm, investmentDto);
        investmentDto.setCondit("普通");
        investmentDto.addTodoDto();
        investmentRepository.save(investmentDto);
    }

    /** 価格変動処理 */
    public void fluctuation() {
        List<InvestmentDto> investList = investmentRepository.findByList();
        int totalPrice = 0;

        for (InvestmentDto investDto: investList) {
            //商品名がアベレージだった場合は平均価格を設定し、
            //それ以外の場合は価格乱数処理を行う
            if ("アベレージ".equals(investDto.getName())) {
                investDto.setPrice(totalPrice / (investList.size() - 1));
                investmentRepository.save(investDto);
                addInvestLog(investDto);
                System.out.println("アベレージ:" + investDto.getPrice()); 
            } else {
                investmentRepository.save(randomPrice(investDto, investList.indexOf(investDto)));
                addInvestLog(investDto);
                totalPrice += investDto.getPrice();
            }
        }
        //取引ログの削除処理
        deleteInvestLog();
        System.out.println();
    }

    /** 取引価格変動ログ追加 */
    public void addInvestLog(InvestmentDto investDto) {
        InvestLogDto investLogDto = new InvestLogDto();

        investLogDto.setInvestId(investDto.getId());
        investLogDto.setInvestName(investDto.getName());
        investLogDto.setPrice(investDto.getPrice());
        investLogDto.setDate(getNowLocalDateTime());
        investLogDto.addTodoDto();

        investLogRepository.save(investLogDto);
    }

    /** 現在日時取得 */
    public LocalDateTime getNowLocalDateTime() {
        //DateTimeFormatterクラスのオブジェクトを生成する
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        //現在日時を取得し、フォーマット変換する
        LocalDateTime nowDefaltDate = LocalDateTime.now();
        LocalDateTime nowDateTime = LocalDateTime.parse(nowDefaltDate.format(dtf), dtf);
        return nowDateTime;
    }

    /** 容量を超えたログを削除する。 */
    public void deleteInvestLog() {
        List<InvestLogDto> investLogDtoList = investLogRepository.findOrderByDateDescList();
        List<InvestmentDto> investmentDtoList = investmentRepository.findAll();
        int logSize = investLogDtoList.size();
        int investSize = investmentDtoList.size();
        //取引ログが300以上の場合は削除処理を行う
        if (logSize > 300) {
            //取引ログが取引数以上300を超えていたら、その分多く削除する。
            if (logSize > 300 + investSize) {
                investSize = investSize * 2;
            }
            for (int i = logSize - 1; i >= logSize - investSize; i--) {
                investLogRepository.delete(investLogDtoList.get(i));
            }
        }
    }

    /** 価格乱数処理 */
    public InvestmentDto randomPrice(InvestmentDto investDto, int index) {
        Random rand = new Random();
        int maxPrice = investDto.getMaxPrice();
        int minPrice = investDto.getMinPrice();
        int rangePrice = (maxPrice - minPrice) / 2;
        int condition = rangePrice * condition(investDto.getCondit()) / 100;
        int minRangePrice = rangePrice / 2;
        int price = investDto.getPrice();
        int change = 0;

        //価格が0だった場合は最小価格を基準に価格乱数を生成する
        if (price == 0) {
            price = rand.nextInt(rangePrice) + (minPrice + minRangePrice + condition);
        } else {
            price = rand.nextInt(rangePrice) + (price - minRangePrice + condition);
        }

        System.out.print(investDto.getName() + " " + investDto.getCondit() + " rand:" + price);
        //暴落時
        if (price < investDto.getCrash()) {
            System.out.print(" 暴落");
            price = rand.nextInt(rangePrice) + (minPrice + minRangePrice);
            investDto.setMaxPrice(maxPrice - minRangePrice);
            investDto.setMinPrice(minPrice - minRangePrice / 2);
            investDto.setCrash(investDto.getCrash() - minRangePrice / 2);
            change -= minRangePrice;
            investDto.setCondit("普通");
            List<BuyingDto> buyingList = buyingRepository.findByInvestIdList(investDto.getId());

            //暴落した商品を売却する
            for (BuyingDto buyingDto: buyingList) {
                UserDto userDto = userRepository.findById(buyingDto.getUserId()).get();
                int quantity = buyingDto.getQuantity();
                if (quantity != 1) {
                    quantity /= 2;
                }
                int money = quantity * investDto.getMinPrice();
                userDto.setMoney(userDto.getMoney() + money);
                userRepository.save(userDto);
                buyingDto.setQuantity(buyingDto.getQuantity() - quantity);
                if (buyingDto.getQuantity() == 0) {
                    buyingRepository.delete(buyingDto);
                } else {
                    buyingRepository.save(buyingDto);
                }
                
            }
        }

        // 価格が最大価格より上の場合、ペナルティ
        if (price - maxPrice > minRangePrice) {
            investDto.setCondit("不調");
            investDto.setMaxPrice(maxPrice + minRangePrice);
            investDto.setMinPrice(minPrice + minRangePrice / 2);
            investDto.setCrash(investDto.getCrash() + minRangePrice / 2);
            change += minRangePrice;
        } else {
            updateCondition(investDto);
        }
        investDto.setPrice(price);
        System.out.print(" set:" + price + " " + investDto.getCondit() + " " + change + "     ");
        if (index % 2 == 1) {
            System.out.println();
        }
        return investDto;
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
    public void updateCondition(InvestmentDto investDto) {
        Random rand = new Random();
        int random = rand.nextInt(40) + 1;
        String condit = investDto.getCondit();

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

        investDto.setCondit(condit);
    }

    /** 購入処理 */
    public void buying(UserDto userDto, String id, int quantity) {
        InvestmentDto investDto = investmentRepository.findById(id).get();
        String userId = userDto.getUserId();
        BuyingDto buyingDto = buyingRepository.findByInvestIdAndUserIdDto(id, userId);

        //購入済みの商品の場合は購入数を足す
        if (buyingDto == null) {
            buyingDto = new BuyingDto();
            buyingDto.addTodoDto();
            buyingDto.setInvestId(id);
            buyingDto.setInvestName(investDto.getName());
            buyingDto.setUserId(userId);
            buyingDto.setUserName(userDto.getUserName());
            buyingDto.setQuantity(quantity);
            buyingDto.setBuyPrice(investDto.getPrice());
        } else {
            quantity = quantity + buyingDto.getQuantity();
            buyingDto.setQuantity(quantity);
            buyingDto.setBuyPrice(investDto.getPrice());
        }

        int totalPrice = investDto.getPrice() * quantity;
        int money = userDto.getMoney();
        if (money < totalPrice) {
            return;
        }
        buyingRepository.save(buyingDto);

        //所持金から合計購入価格を差し引く
        userDto.setMoney(money - totalPrice);
        userRepository.save(userDto);
    }

    /** 売却処理 */
    public void selling(UserDto userDto, String id, int quantity) {
        InvestmentDto investDto = investmentRepository.findById(id).get();
        String userId = userDto.getUserId();
        BuyingDto buyingDto = buyingRepository.findByInvestIdAndUserIdDto(id, userId);

        try {
            //売却個数が購入個数より多い場合は、処理を中断
            if (quantity > buyingDto.getQuantity()) {
                return;
            } else if (quantity == buyingDto.getQuantity()) {
                buyingRepository.delete(buyingDto);
            } else {
                buyingDto.setQuantity(buyingDto.getQuantity() - quantity);
                buyingRepository.save(buyingDto);
            }
        } catch (Exception e) {
            System.out.println("例外発生!!");
            int money = quantity * investDto.getPrice();
            userDto.setMoney(userDto.getMoney() + money);
            userRepository.save(userDto);
            return;
        }

        //所持金に合計売却価格を足す
        int money = quantity * investDto.getPrice();
        userDto.setMoney(userDto.getMoney() + money);
        userRepository.save(userDto);

        //売却履歴作成
        SellingDto sellingDto = new SellingDto();
        sellingDto.setInvestId(id);
        sellingDto.setUserId(userId);
        sellingDto.setInvestName(investDto.getName());
        sellingDto.setUserName(userDto.getUserName());
        sellingDto.setSellPrice(money);
        sellingDto.setDate(getNowLocalDateTime());
        sellingDto.addTodoDto();
        sellingRepository.save(sellingDto);
        deleteSelling();
    }

    /** 容量を超えた売却履歴を削除 */
    public void deleteSelling() {
        List<SellingDto> sellingDtoList = sellingRepository.findOrderByDateDescList();
        int size = sellingDtoList.size();

        if (size > 300) {
            int deleteSize = size - 300;
            for (int i = size - 1; i > size - deleteSize; i--) {
                sellingRepository.delete(sellingDtoList.get(i));
            }
        }
    }

    /** 自動取引処理 */
    public void autoInvestment(UserDto userDto) {
        List<InvestmentDto> investDtoList = investmentRepository.findAll();
        int count;
        
        if (rand() == 0) {
            for (InvestmentDto investDto: investDtoList) {
                if ("絶不調".equals(investDto.getCondit()) || "不調".equals(investDto.getCondit()) || "アベレージ".equals(investDto.getName())) {
                    continue;
                }
                count = buyCheck(investDto, userDto.getMoney());
                if (count > 0) {
                    buying(userDto, investDto.getId(), count);
                }
            }
        }
        List<BuyingDto> buyingDtoList = buyingRepository.findByUserIdList(userDto.getUserId());

        for (BuyingDto buyingDto: buyingDtoList) {
            InvestmentDto investDto = investmentRepository.findById(buyingDto.getInvestId()).get();
            if ("絶好調".equals(investDto.getCondit()) || "アベレージ".equals(investDto.getName())) {
                continue;
            }
            if (sellCheck(investDto)) {
                selling(userDto, investDto.getId(), buyingDto.getQuantity());
            }
        }
    }

    /** 自動購入チェック */
    public int buyCheck(InvestmentDto investDto, int money) {
        int count = 0;
        int maxPrice = investDto.getMaxPrice();
        int minPrice = investDto.getMinPrice();
        int rangePrice = (maxPrice - minPrice) / 2;
        int minRangePrice = rangePrice / 2;
        int price = investDto.getPrice();
        String condit = investDto.getCondit();

        if (minPrice + minRangePrice < price && price < minPrice + rangePrice) {
            count++;
        }
        if ("好調".equals(condit)) {
            count++;
        }
        if ("絶好調".equals(condit)) {
            count += rand() + 1;
        }
        if (count > 0 && money > price * 10) {
            count += rand() + 1;
        }
        return count;
    }

    /** 自動売却チェック */
    public boolean sellCheck(InvestmentDto investDto) {
        boolean flg = false;
        int maxPrice = investDto.getMaxPrice();
        int minPrice = investDto.getMinPrice();
        int rangePrice = (maxPrice - minPrice) / 2;
        int minRangePrice = rangePrice / 2;
        int price = investDto.getPrice();
        String condit = investDto.getCondit();

        if ("絶不調".equals(condit)) {
            flg = true;
        }
        if ("好調".equals(condit) && price > maxPrice) {
            flg = true;
        }
        if ("普通".equals(condit) && price > maxPrice - minRangePrice) {
            flg = true;
        }
        if ("不調".equals(condit) && price > minPrice + rangePrice) {
            flg = true;
        }
        
        return flg;
    }

    /** 乱数処理 */
    public int rand() {
        Random rand = new Random();
        return rand.nextInt(3);
    }
}
