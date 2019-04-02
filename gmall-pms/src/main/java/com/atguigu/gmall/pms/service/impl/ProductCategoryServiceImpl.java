package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.entity.ProductCategory;
import com.atguigu.gmall.pms.entity.ProductCategoryAttributeRelation;
import com.atguigu.gmall.pms.mapper.ProductCategoryAttributeRelationMapper;
import com.atguigu.gmall.pms.mapper.ProductCategoryMapper;
import com.atguigu.gmall.pms.service.ProductCategoryService;
import com.atguigu.gmall.pms.vo.PmsProductCategoryParam;
import com.atguigu.gmall.pms.vo.PmsProductCategoryWithChildrenItem;
import com.atguigu.gmall.utils.PageToMap;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * <p>
 * 产品分类 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {
    @Autowired
    ProductCategoryAttributeRelationMapper productCategoryAttributeRelationMapper;
    @Autowired
    ProductCategoryMapper productCategoryMapper;

    /**
     * 查询一级分类以及子类
     *
     * @return
     */
    public List<PmsProductCategoryWithChildrenItem> selectOneLevelAndChildrens() {
        List<PmsProductCategoryWithChildrenItem> list = new ArrayList<>();

        //查询出一级分类
        QueryWrapper<ProductCategory> qw1 = new QueryWrapper<ProductCategory>();
        qw1.eq("level", "0");
        List<ProductCategory> oneLevels = baseMapper.selectList(qw1);
        //查询出二级分类
        QueryWrapper<ProductCategory> qw2 = new QueryWrapper<ProductCategory>();
        qw2.eq("level", "1");
        List<ProductCategory> twoLevels = baseMapper.selectList(qw2);
        //查询出三级分类
        QueryWrapper<ProductCategory> qw3 = new QueryWrapper<ProductCategory>();
        qw3.eq("level", "2");
        List<ProductCategory> threeLevels = baseMapper.selectList(qw3);
        //----遍历一级分类
        for (int i = 0; i < oneLevels.size(); i++) {
            ProductCategory oneLevel = oneLevels.get(i);//获取一级分类信息
            PmsProductCategoryWithChildrenItem Oneitem = new PmsProductCategoryWithChildrenItem();
            BeanUtils.copyProperties(oneLevel,Oneitem);//组装数据
            List<PmsProductCategoryWithChildrenItem> listOne = new ArrayList<>();
            //遍历二级分类
            for (int j = 0; j < twoLevels.size(); j++) {
                ProductCategory twoLevel = twoLevels.get(j);//获取二级分类信息
                PmsProductCategoryWithChildrenItem twoitem = new PmsProductCategoryWithChildrenItem();
                if(twoLevel.getParentId()==oneLevel.getId()){//是一级分类下的二级分类
                    BeanUtils.copyProperties(twoLevel,twoitem);//封装成对象
                    List<PmsProductCategoryWithChildrenItem> listTwo = new ArrayList<>();
                    //listOne.add(twoitem);//封装普通属性
                    //封装twoitem的子属性
                    for (int k = 0; k < threeLevels.size(); k++) {
                        ProductCategory threeLevel = threeLevels.get(k);//三级分类
                        PmsProductCategoryWithChildrenItem threeitem = new PmsProductCategoryWithChildrenItem();
                        if(threeLevel.getParentId()==twoLevel.getId()){
                            BeanUtils.copyProperties(threeLevel,threeitem);//封装成对象
                            listTwo.add(threeitem);//封装三级分类到二级分类的子属性中
                        }
                    }//三级分类组装完成
                    twoitem.setChildren(listTwo);
                    listOne.add(twoitem);

                }
            }//二级分类组装完成
            //将一级分类的子项目组装
            Oneitem.setChildren(listOne);
            list.add(Oneitem);
        }
        return list;
    }

    /**
     * 查询商品分类列表
     * @param parentId
     * @param pageSize
     * @param pageNum
     * @return
     */
    @Override
    public HashMap<String, Object> selectProductCategoryList(Long parentId, Integer pageSize, Integer pageNum) {
        Page<ProductCategory> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ProductCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",parentId);
        baseMapper.selectPage(page,queryWrapper);
        HashMap<String, Object> map = PageToMap.toMap(page);
        return map;
    }

    /**
     * 增加商品分类
     * 1在product_categroy中增加获得id值
     * 2在relation表中添加属性列表
     * @param productCategory
     */
    @Override
    public boolean  addProductCategroy(PmsProductCategoryParam pmsProductCategoryParam) {
        ProductCategory productCategory = new ProductCategory();
        BeanUtils.copyProperties(pmsProductCategoryParam,productCategory);
        //查看是几级分类
        Long parentId = productCategory.getParentId();//7
        if(parentId==0){
            productCategory.setLevel(0);
        }else{
            ProductCategory parentProduct = baseMapper.selectById(parentId);//父类

            if(parentProduct.getLevel()==1){
                productCategory.setLevel(2);
            }else if(parentProduct.getLevel()==2){
                productCategory.setLevel(3);
            }else{
                productCategory.setLevel(0);
            }
        }

        baseMapper.insert(productCategory);
        Long id = productCategory.getId();//获得Id
        List<Long> productAttributeIdList = pmsProductCategoryParam.getProductAttributeIdList();
        boolean b = productCategoryAttributeRelationMapper.insertBatch(id, productAttributeIdList);
        return b;
    }

    /**
     * 删除商品分类
     * @param id
     * @return
     */
    @Override
    public boolean deleteById(Long id) {
        int i = baseMapper.deleteById(id);
        return i>0;
    }

    /**
     * 批量更改显示状态
     * @param ids
     * @param showStatus
     * @return
     */
    @Override
    public boolean updateShowStatus(List<Long> ids, Integer showStatus) {
        boolean b = productCategoryMapper.updateShowStatusBatch(ids, showStatus);
        return b;
    }

    @Override
    public boolean updateNavStatus(List<Long> ids, Integer navStatus) {
        boolean b = productCategoryMapper.updateNavStatusBatch(ids, navStatus);
        return b;
    }

    /**
     * 修改商品分类信息
     * 先修改本表，再改关联表
     * 改关联表：先删除旧的，再增加新的
     * @param id
     * @param productCategoryParam
     * @return
     */
    @Override
    public boolean updateProductCategroyById(Long id, PmsProductCategoryParam productCategoryParam) {
        ProductCategory productCategory = new ProductCategory();
        BeanUtils.copyProperties(productCategoryParam,productCategory);
        productCategory.setId(id);
        baseMapper.updateById(productCategory);
        List<Long> list = productCategoryParam.getProductAttributeIdList();
        //删除原有的，添加新的
        boolean b = true;
        if(list.size()>0){
            ProductCategoryAttributeRelation productCategoryAttributeRelation = new ProductCategoryAttributeRelation();
            QueryWrapper<ProductCategoryAttributeRelation> queryWrapper = new QueryWrapper();
            queryWrapper.eq("product_category_id", id);//查询
            //在关联表中的所有数据删除掉
            productCategoryAttributeRelationMapper.delete(queryWrapper);
            //添加新的
            b = productCategoryAttributeRelationMapper.insertBatch(id, list);
        }
        return b;
    }

}
