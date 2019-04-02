package com.atguigu.gmall.sms.vo;

import com.atguigu.gmall.sms.entity.Coupon;
import com.atguigu.gmall.sms.entity.CouponProductCategoryRelation;
import com.atguigu.gmall.sms.entity.CouponProductRelation;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.sun.org.apache.bcel.internal.generic.NEW;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
@Data
public class SmsCouponCategoryWithList extends Coupon implements Serializable{
    List<CouponProductCategoryRelation> productCategoryRelationList = new ArrayList<>();
    List<CouponProductRelation> productRelationList = new VirtualFlow.ArrayLinkedList<>();
}
