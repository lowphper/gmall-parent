package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.Brand;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 品牌表 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface BrandService extends IService<Brand> {

    List<Brand> getAllBrand();

    boolean deleteBrandById(Long id);

    boolean updateBrandById( Brand brand);

    boolean addBrandOne(Brand brand);
    Map<String,Object> getBrandList(String keyword, Integer pageNum, Integer pageSize);

    boolean deleteBrandBatchById(List<Long> ids);

    boolean updateShowStatusBatch(List<Long> ids,Integer showStatus);

    boolean updateFactoryStatusBatch(List<Long> ids, Integer factoryStatus);
}
