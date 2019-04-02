package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.vo.PmsProductParam;
import com.atguigu.gmall.pms.vo.PmsProductQueryParam;
import com.atguigu.gmall.to.es.EsProductAttributeValue;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品信息 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface ProductService extends IService<Product> {

    Map<String,Object> selectPrducts(PmsProductQueryParam pmsProductQueryParam, Integer pageSize, Integer pageNum);

    boolean updateProductById(Long id, PmsProductParam productParam);

    Map<String,Object> selectProductByNameOrProductSn(String keyword);

    boolean deleteProductBatch(List<Long> ids, Integer deleteStatus);

    boolean updateVerifyStatusBatch(List<Long> ids, Integer verifyStatus, String detail);

    boolean updatePublishStatusBatch(List<Long> ids, Integer publishStatus);

    boolean updateRecommendStatusBatch(List<Long> ids, Integer recommendStatus);

    boolean updateNewStatusBatch(List<Long> ids, Integer newStatus);

    boolean saveProduct(PmsProductParam productParam);

    PmsProductParam getProductInfoById(Long id);

    boolean updateProductAndOtherInfoById(Long id, PmsProductParam productParam);

    List<EsProductAttributeValue> getProductSaleAttr(Long productId);

    List<EsProductAttributeValue> getProductBaseAttr(Long productId);

    Product selectProductFromCache(Long productId);
}
