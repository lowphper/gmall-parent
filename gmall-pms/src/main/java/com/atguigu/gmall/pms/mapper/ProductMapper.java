package com.atguigu.gmall.pms.mapper;

import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.to.es.EsProductAttributeValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 商品信息 Mapper 接口
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface ProductMapper extends BaseMapper<Product> {
    boolean deleteProductBatch(List<Long> list);

    boolean updateVerifyStatusBatch(@Param("list") List<Long> ids, @Param("verifyStatus") Integer verifyStatus);

    boolean updatePublishStatusBatch(@Param("list") List<Long> ids, @Param("publishStatus")Integer publishStatus);

    boolean updateRecommendStatusBatch(@Param("list") List<Long> ids, @Param("recommendStatus")Integer recommendStatus);

    boolean updateNewStatusBatch(@Param("list") List<Long> ids, @Param("newStatus")Integer newStatus);

    List<EsProductAttributeValue> getProductSaleAttr(Long productId);

    List<EsProductAttributeValue> getProductBaseAttr(Long productId);
}
