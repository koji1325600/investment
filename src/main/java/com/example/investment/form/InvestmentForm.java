package com.example.investment.form;

import lombok.Data;

@Data
public class InvestmentForm {
    /** ID */
    private String id;

    /** 名前 */
    private String name;

    /** 最大価格 */
    private int maxPrice;

    /** 最小価格 */
    private int minPrice;

    /** 価格 */
    private int price;

    /** 暴落設定 */
    private int crash;

    /** 調子 */
    private String condit;
}
