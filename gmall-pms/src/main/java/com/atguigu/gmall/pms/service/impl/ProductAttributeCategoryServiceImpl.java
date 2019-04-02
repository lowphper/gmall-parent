package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.entity.ProductAttribute;
import com.atguigu.gmall.pms.entity.ProductAttributeCategory;
import com.atguigu.gmall.pms.mapper.ProductAttributeCategoryMapper;
import com.atguigu.gmall.pms.service.ProductAttributeCategoryService;
import com.atguigu.gmall.pms.service.ProductAttributeService;
import com.atguigu.gmall.pms.vo.PmsProductAttributeCategoryItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import sun.rmi.runtime.Log;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 产品属性分类表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
public class ProductAttributeCategoryServiceImpl extends ServiceImpl<ProductAttributeCategoryMapper, ProductAttributeCategory> implements ProductAttributeCategoryService {
    @Autowired
    ProductAttributeServiceImpl ProductAttributeService;
    /**
     * 分页获取所有属性
     *
     * @param pageSize
     * @param pageNum
     * @return
     */
    @Override
    public  HashMap<String, Object> selectproductAttributeCategoryPage(Integer pageSize, Integer pageNum) {
        Page<ProductAttributeCategory> page = new Page<>(pageNum,pageSize);
        baseMapper.selectPage(page, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("total",page.getTotal());
        map.put("totalPage",page.getPages());
        map.put("pageSize",pageSize);
        map.put("list",page.getRecords());
        System.out.println(page.getRecords());
        return map;
    }

    /**
     * 修改商品属性
     * @param id
     * @param name
     * @return
     */
    @Override
    public boolean updateAttributeCategoryById(Long id, String name) {
        ProductAttributeCategory productAttributeCategory = new ProductAttributeCategory();
        productAttributeCategory.setId(id);
        productAttributeCategory.setName(name);
        int i = baseMapper.updateById(productAttributeCategory);
        return i>0;
    }

    /**
     * 根据id获取商品属性信息
     * @param id
     * @return
     */
    @Override
    public ProductAttributeCategory getAttributeCategoryById(Long id) {
        ProductAttributeCategory productAttributeCategory = baseMapper.selectById(id);
        return productAttributeCategory;
    }

    /**
     * 添加属性分类
     * @param name
     */
    @Override
    public void saveAttributeCategory(String name) {
        ProductAttributeCategory productAttributeCategory = new ProductAttributeCategory();
        productAttributeCategory.setName(name);
        baseMapper.insert(productAttributeCategory);
    }

    /**
     * 删除属性分类
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        baseMapper.deleteById(id);
    }

    /**
     * 获取属性分类以及其下的属性
     * @return
     */
    @Override
    public List<PmsProductAttributeCategoryItem> getAttributeCategoryAndChildren() {
        List<PmsProductAttributeCategoryItem> lists = new ArrayList<>();
        //获取所有属性
        List<ProductAttributeCategory> list = baseMapper.selectList(null);
        //属性下的属性
        BaseMapper<ProductAttribute> attributeMapper = ProductAttributeService.getBaseMapper();

        List<ProductAttribute> attributeList = attributeMapper.selectList(null);//获取所有二级属性
        for (int i = 0; i < list.size(); i++) {
            ProductAttributeCategory cate = list.get(i);//一级属性
            PmsProductAttributeCategoryItem pac = new PmsProductAttributeCategoryItem();
            List<ProductAttribute> proList = new ArrayList<>();
            for (int j = 0; j < attributeList.size(); j++) {
                ProductAttribute attr = attributeList.get(j);
                if(attr.getProductAttributeCategoryId()==cate.getId()){
                    proList.add(attr);
                }
            }
            BeanUtils.copyProperties(cate,pac);
            pac.setProductAttributeList(proList);
            lists.add(pac);
        }

        return lists;
    }
}
