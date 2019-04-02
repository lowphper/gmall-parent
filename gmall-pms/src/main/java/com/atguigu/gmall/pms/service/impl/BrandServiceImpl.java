package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.entity.Brand;
import com.atguigu.gmall.pms.mapper.BrandMapper;
import com.atguigu.gmall.pms.service.BrandService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 品牌表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {
   @Autowired
   BrandMapper brandMapper;
    /**
     * 查询所有商标
     * @return
     */
    @Override
    public List<Brand> getAllBrand() {

        List<Brand> list = baseMapper.selectList(null);
        return list;
    }
    /**
     *修改商标
     */
    public boolean updateBrandById(Brand brand){
        int i = baseMapper.updateById(brand);
        return i>0;

    }
    /**
     * 删除商标
     */
    public boolean deleteBrandById(Long id){
        int i = baseMapper.deleteById(id);
        return i>0;
    }

    /**
     * 增加商标
     *
     */
    public boolean addBrandOne(Brand brand){
        int insert = baseMapper.insert(brand);
        return insert>0;
    }

    /**
     * 根据关键字分页获取品牌
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public Map<String,Object> getBrandList(String keyword, Integer pageNum, Integer pageSize) {
        QueryWrapper<Brand> queryWrapper = null;

        if(keyword!=null){
            queryWrapper = new QueryWrapper();
            queryWrapper.like("name",keyword);
        }
        Page<Brand> page = new Page<>(pageNum, pageSize);
        baseMapper.selectPage(page,queryWrapper);
        HashMap<String, Object> map = new HashMap<>();
        map.put("total",page.getTotal());
        map.put("totalPage",page.getPages());
        map.put("pageSize",pageSize);
        map.put("list",page.getRecords());
        return map;
    }

    /**
     * 批量删除品牌
     * @param ids
     * @return
     */
    @Override
    public boolean deleteBrandBatchById(List<Long> ids) {
        int i = baseMapper.deleteBatchIds(ids);
        return i>0;
    }

    /**
     * 批量设置是否显示
     * @param ids
     * @return
     */
    @Override
    public boolean updateShowStatusBatch(List<Long> ids,Integer showStatus) {
        boolean b;
        if(showStatus==0){
            b = brandMapper.updateBrandShowStatusToHidden(ids);
        }else{
            b = brandMapper.updateBrandShowStatusToShow(ids);
        }
        return b;
    }

    @Override
    public boolean updateFactoryStatusBatch(List<Long> ids, Integer factoryStatus) {
        boolean b;
        if(factoryStatus==0){
            b = brandMapper.updateBrandFactoryStatusToHidden(ids);
        }else{
            b = brandMapper.updateBrandFactoryStatusToShow(ids);
        }
        return b;

    }
}
