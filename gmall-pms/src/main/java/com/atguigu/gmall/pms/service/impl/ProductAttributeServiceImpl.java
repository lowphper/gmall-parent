package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.entity.ProductAttribute;
import com.atguigu.gmall.pms.mapper.ProductAttributeMapper;
import com.atguigu.gmall.pms.service.ProductAttributeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;


/**
 * <p>
 * 商品属性参数表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
@Component
public class ProductAttributeServiceImpl extends ServiceImpl<ProductAttributeMapper, ProductAttribute> implements ProductAttributeService {

    /**
     * 根据分类获取属性或参数列表
     * @param type
     * @param pageSize
     * @param pageNum
     * @return
     */
    @Override
    public HashMap<String, Object> getAttributeBy0Or1(Long cid,Integer type, Integer pageSize, Integer pageNum) {
        QueryWrapper<ProductAttribute> query = new QueryWrapper<>();
        query.eq("type",type);
        query.eq("product_attribute_category_id",cid);
        Page<ProductAttribute> page = new Page<>(pageNum,pageSize);
        baseMapper.selectPage(page,query);

        HashMap<String, Object> map = new HashMap<>();
        map.put("total",page.getTotal());
        map.put("totalPage",page.getPages());
        map.put("pageSize",pageSize);
        map.put("list",page.getRecords());
        map.put("pageNum",pageNum);

        return map;
    }
}
