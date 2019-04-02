package com.atguigu.gmall.sms.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SmsCouponQueryParam implements Serializable{
    private String name;
    private Integer type;
}
