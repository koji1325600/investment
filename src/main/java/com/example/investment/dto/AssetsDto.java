package com.example.investment.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="assets")
public class AssetsDto implements Serializable {
    /** デフォルトシリアルUID */
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    private String id;

    /** ユーザID */
    @Column
    private String userId;

    /** ユーザ名 */
    @Column
    private String userName;

    /** 価格 */
    @Column
    private int price;

    /** 日時 */
    @Column
    private LocalDateTime date;

    /**
     * デフォルトコンストラクタ。
     */
    public void addTodoDto() {
        this.id = UUID.randomUUID().toString();
    }
}
