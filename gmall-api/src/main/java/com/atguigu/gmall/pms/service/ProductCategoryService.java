package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.ProductCategory;
import com.atguigu.gmall.pms.vo.PmsProductCategoryParam;
import com.atguigu.gmall.pms.vo.PmsProductCategoryWithChildrenItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.HashMap;
import java.util.List;


/**
 * <p>
 * 产品分类 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface ProductCategoryService extends IService<ProductCategory> {

    List<PmsProductCategoryWithChildrenItem> selectOneLevelAndChildrens();

    HashMap<String, Object> selectProductCategoryList(Long parentId, Integer pageSize, Integer pageNum);

    boolean  addProductCategroy(PmsProductCategoryParam productCategory);

    boolean deleteById(Long id);

    boolean updateShowStatus(List<Long> ids, Integer showStatus);

    boolean updateNavStatus(List<Long> ids, Integer navStatus);

    boolean updateProductCategroyById(Long id, PmsProductCategoryParam productCategoryParam);
}
