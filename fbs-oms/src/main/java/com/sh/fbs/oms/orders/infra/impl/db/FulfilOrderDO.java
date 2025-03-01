package com.sh.fbs.oms.orders.infra.impl.db;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;



@Data
@TableName("fulfil_orders")
public class FulfilOrderDO {

    private String orderId;

    private Integer userId;

    private Integer shopId;

    private String shopName;

    private String userName;

    private Short orderStatus;

    private BigDecimal orderAmount;

    private BigDecimal discountAmount;

    private BigDecimal paidAmount;

    private Integer itemLines;

    private Date createTime;

    private Date updateTime;

    private String shippingAddress;

    private String paymentMethod;

}