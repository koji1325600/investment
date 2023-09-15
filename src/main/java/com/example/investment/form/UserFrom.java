package com.example.investment.form;

import lombok.Data;

@Data
public class UserFrom {
    /** ID */
    private String userId;

    /** 名前 */
    private String userName;

    /** パスワード */
    private String password;

    /** メールアドレス */
    private String mailaddress;

    /** 所持金 */
    private int money;
}
