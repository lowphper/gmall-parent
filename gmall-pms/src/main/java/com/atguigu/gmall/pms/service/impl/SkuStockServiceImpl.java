package com.atguigu.gmall.pms.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.entity.SkuStock;
import com.atguigu.gmall.pms.mapper.SkuStockMapper;
import com.atguigu.gmall.pms.service.SkuStockService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * sku的库存 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Component
@Service
public class SkuStockServiceImpl extends ServiceImpl<SkuStockMapper, SkuStock> implements SkuStockService {
    /**
     * 批量更新库存信息
     * @param pid
     * @param skuStockList
     * @return
     */
    @Override
    public boolean updateSkuStockBatch(Long pid, List<SkuStock> skuStockList) {
        for (int i = 0; i < skuStockList.size(); i++) {
            SkuStock skuStock = skuStockList.get(i);
            baseMapper.updateById(skuStock);
        }
        return true;
    }

    /**
     * 根据商品编号或库存编号模糊搜索库存
     * @param pid
     * @param keyword
     * @return
     */
    @Override
    public List<SkuStock> selectSkuStockByPidOrkeyword(Long pid, String keyword) {
        QueryWrapper<SkuStock> skuStockQueryWrapper = new QueryWrapper<>();
        skuStockQueryWrapper.eq("product_id",pid);
        if(keyword!=null){
            skuStockQueryWrapper.eq("sku_code",keyword);
        }
        List<SkuStock> list = baseMapper.selectList(skuStockQueryWrapper);
        return list;
    }

    @Override
    public SkuStock selectSkuStockById(Long skuId) {
        System.out.println("进到selectSkuStockById方法");
        return baseMapper.selectById(skuId);
    }
}
