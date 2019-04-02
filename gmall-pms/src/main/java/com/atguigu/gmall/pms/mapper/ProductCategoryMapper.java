package com.atguigu.gmall.pms.mapper;

import com.atguigu.gmall.pms.entity.ProductCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 产品分类 Mapper 接口
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface ProductCategoryMapper extends BaseMapper<ProductCategory> {

    boolean updateShowStatusBatch(@Param("list") List<Long> ids, @Param("showStatus")Integer showStatus);
    boolean updateNavStatusBatch(@Param("list") List<Long> ids, @Param("navStatus")Integer navStatus);

    void updateCountById(Long id);
}
