package com.atguigu.gmall.pms.mapper;

import com.atguigu.gmall.pms.entity.Brand;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 品牌表 Mapper 接口
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */

public interface BrandMapper extends BaseMapper<Brand> {

    boolean updateBrandShowStatusToHidden(List<Long> ids);
    boolean updateBrandShowStatusToShow(List<Long> ids);
    boolean updateBrandFactoryStatusToHidden(List<Long> ids);
    boolean updateBrandFactoryStatusToShow(List<Long> ids);
}
