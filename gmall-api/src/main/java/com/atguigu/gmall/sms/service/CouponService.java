package com.atguigu.gmall.sms.service;

import com.atguigu.gmall.sms.entity.Coupon;
import com.atguigu.gmall.sms.vo.SmsCouponCategoryWithList;
import com.atguigu.gmall.sms.vo.SmsCouponQueryParam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 优惠卷表 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface CouponService extends IService<Coupon> {

    Map<String, Object> listCoupon(SmsCouponQueryParam smsCouponQueryParam, Integer pageSize,Integer pageNum);


    SmsCouponCategoryWithList getCouponDetileById(Long id);

    boolean updateCouponById(Long id, SmsCouponCategoryWithList smsCouponCategoryWithList);

    boolean createCoubon(SmsCouponCategoryWithList smsCouponCategoryWithList);
}
