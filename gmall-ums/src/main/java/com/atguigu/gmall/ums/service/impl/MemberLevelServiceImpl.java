package com.atguigu.gmall.ums.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.ums.entity.MemberLevel;
import com.atguigu.gmall.ums.mapper.MemberLevelMapper;
import com.atguigu.gmall.ums.service.MemberLevelService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 会员等级表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
@Component
public class MemberLevelServiceImpl extends ServiceImpl<MemberLevelMapper, MemberLevel> implements MemberLevelService {

    /**
     * 获取会员等级
     * @param defaultStatus
     * @return
     */
    @Override
    public List<MemberLevel> getMemberLevelByStatus(String defaultStatus) {
        QueryWrapper<MemberLevel> memberLevelQueryWrapper = new QueryWrapper<>();
        memberLevelQueryWrapper.eq("default_status",defaultStatus);
        List<MemberLevel> memberLevels = baseMapper.selectList(memberLevelQueryWrapper);
        return memberLevels;
    }
}
