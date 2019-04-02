package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.ProductAttributeCategory;
import com.atguigu.gmall.pms.vo.PmsProductAttributeCategoryItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 产品属性分类表 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface ProductAttributeCategoryService extends IService<ProductAttributeCategory> {

    HashMap<String, Object> selectproductAttributeCategoryPage(Integer pageSize, Integer pageNum);

    boolean updateAttributeCategoryById(Long id, String name);

    ProductAttributeCategory getAttributeCategoryById(Long id);

    void saveAttributeCategory(String name);

    void deleteById(Long id);

    List<PmsProductAttributeCategoryItem> getAttributeCategoryAndChildren();
}
