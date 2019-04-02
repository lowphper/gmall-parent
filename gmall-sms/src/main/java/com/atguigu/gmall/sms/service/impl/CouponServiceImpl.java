package com.atguigu.gmall.sms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.sms.entity.Coupon;
import com.atguigu.gmall.sms.entity.CouponProductCategoryRelation;
import com.atguigu.gmall.sms.entity.CouponProductRelation;
import com.atguigu.gmall.sms.mapper.CouponMapper;
import com.atguigu.gmall.sms.mapper.CouponProductCategoryRelationMapper;
import com.atguigu.gmall.sms.mapper.CouponProductRelationMapper;
import com.atguigu.gmall.sms.service.CouponService;
import com.atguigu.gmall.sms.vo.SmsCouponCategoryWithList;
import com.atguigu.gmall.sms.vo.SmsCouponQueryParam;
import com.atguigu.gmall.utils.PageToMap;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 优惠卷表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {
    @Autowired
    CouponProductRelationMapper couponProductRelationMapper;
    @Autowired
    CouponProductCategoryRelationMapper couponProductCategoryRelationMapper;

    /**
     * 查询所有优惠券
     *
     * @return
     */
    @Override
    public Map<String, Object> listCoupon(SmsCouponQueryParam smsCouponQueryParam, Integer pageSize, Integer pageNum) {
        QueryWrapper<Coupon> couponQueryWrapper = new QueryWrapper<>();
        if (smsCouponQueryParam.getName() != null) {
            couponQueryWrapper.like("name", smsCouponQueryParam.getName());
        }
        if (smsCouponQueryParam.getType() != null) {
            couponQueryWrapper.eq("type", smsCouponQueryParam.getType());
        }
        Page<Coupon> page = new Page<>(pageNum, pageSize);
        baseMapper.selectPage(page, couponQueryWrapper);
        HashMap<String, Object> map = PageToMap.toMap(page);
        return map;
    }

    /**
     * 根据id查询优惠卷详细信息
     *
     * @param id
     * @return
     */
    @Override
    public SmsCouponCategoryWithList getCouponDetileById(Long id) {
        SmsCouponCategoryWithList list = new SmsCouponCategoryWithList();
        //查询优惠券
        Coupon coupon = baseMapper.selectById(id);
        BeanUtils.copyProperties(coupon, list);
        //查询优惠卷与产品关系
        QueryWrapper<CouponProductRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("coupon_id", id);
        List<CouponProductRelation> relationList = couponProductRelationMapper.selectList(queryWrapper);
        list.setProductRelationList(relationList);
        //查询优惠券与产品标签的关系
        QueryWrapper<CouponProductCategoryRelation> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("coupon_id", id);
        List<CouponProductCategoryRelation> couponProductCategoryRelationList = couponProductCategoryRelationMapper.selectList(queryWrapper1);
        list.setProductCategoryRelationList(couponProductCategoryRelationList);
        //返回结果
        return list;
    }

    /**
     * 根据id修改优惠卷信息
     *
     * @param id
     * @param smsCouponCategoryWithList
     * @return
     */
    @Override
    public boolean updateCouponById(Long id, SmsCouponCategoryWithList smsCouponCategoryWithList) {
        Coupon coupon = new Coupon();
        BeanUtils.copyProperties(smsCouponCategoryWithList, coupon);
        coupon.setId(id);
        int insert = baseMapper.insert(coupon);
        //2,删除原有的，填充新的
        List<CouponProductRelation> couponProductRelationList = new ArrayList<>();
        couponProductRelationList = smsCouponCategoryWithList.getProductRelationList();
        QueryWrapper<CouponProductRelation> couponProductRelationQueryWrapper = new QueryWrapper<>();
        couponProductRelationQueryWrapper.eq("coupon_id", smsCouponCategoryWithList.getId());
        int delete = couponProductRelationMapper.delete(couponProductRelationQueryWrapper);
        //2.1 添加新的
        for (int i = 0; i < couponProductRelationList.size(); i++) {
            CouponProductRelation couponProductRelation = couponProductRelationList.get(i);
            couponProductRelation.setCouponId(id);
            couponProductRelationMapper.insert(couponProductRelation);
        }
        //3
        List<CouponProductCategoryRelation> couponProductCategoryRelationList = new ArrayList<>();
        couponProductCategoryRelationList = smsCouponCategoryWithList.getProductCategoryRelationList();
        QueryWrapper<CouponProductCategoryRelation> couponProductCategoryRelationWrapper = new QueryWrapper<>();
        couponProductCategoryRelationWrapper.eq("coupon_id", smsCouponCategoryWithList.getId());
        int delete1 = couponProductCategoryRelationMapper.delete(couponProductCategoryRelationWrapper);
        //3.1 添加新的
        for (int i = 0; i < couponProductCategoryRelationList.size(); i++) {
            CouponProductCategoryRelation couponProductCategoryRelation = couponProductCategoryRelationList.get(i);
            couponProductCategoryRelation.setCouponId(id);
            couponProductCategoryRelationMapper.insert(couponProductCategoryRelation);
        }
        return true;
    }

    /**
     * 创建优惠券
     * 填充主表sms_coupon
     * 填充sms_coupon_product_relation
     * 填充sms_coupon_product_category_relation
     *
     * @param smsCouponCategoryWithList
     * @return
     */
    @Override
    public boolean createCoubon(SmsCouponCategoryWithList smsCouponCategoryWithList) {
        Coupon coupon = new Coupon();
        BeanUtils.copyProperties(smsCouponCategoryWithList, coupon);
        Long id = smsCouponCategoryWithList.getId();
        coupon.setId(id);
        int insert = baseMapper.insert(coupon);
        //2,删除原有的，填充新的
        List<CouponProductRelation> couponProductRelationList = new ArrayList<>();
        couponProductRelationList = smsCouponCategoryWithList.getProductRelationList();
        for (int i = 0; i < couponProductRelationList.size(); i++) {
            CouponProductRelation couponProductRelation = couponProductRelationList.get(i);
            couponProductRelation.setCouponId(id);
            couponProductRelationMapper.insert(couponProductRelation);
        }
        //3
        List<CouponProductCategoryRelation> couponProductCategoryRelationList = new ArrayList<>();
        couponProductCategoryRelationList = smsCouponCategoryWithList.getProductCategoryRelationList();
        for (int i = 0; i < couponProductCategoryRelationList.size(); i++) {
            CouponProductCategoryRelation couponProductCategoryRelation = couponProductCategoryRelationList.get(i);
            couponProductCategoryRelation.setCouponId(id);
            couponProductCategoryRelationMapper.insert(couponProductCategoryRelation);
        }
        return true;
    }

}
