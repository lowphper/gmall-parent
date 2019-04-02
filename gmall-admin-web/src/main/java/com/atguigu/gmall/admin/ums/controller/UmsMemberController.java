package com.atguigu.gmall.admin.ums.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.to.CommonResult;
import com.atguigu.gmall.ums.entity.MemberLevel;
import com.atguigu.gmall.ums.service.MemberLevelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@Api(tags = "MemberController", description = "后台会员管理")
@RequestMapping("/memberLevel")
@Slf4j
public class UmsMemberController {

    @Reference
    private MemberLevelService memberLevelService;

    @ApiOperation(value = "获取会员等级")
    @GetMapping(value = "/list")
    public Object login(@RequestParam(value = "defaultStatus", defaultValue = "0") String defaultStatus) {
        CommonResult commonResult = new CommonResult();
        List<MemberLevel> memberLevels = memberLevelService.getMemberLevelByStatus(defaultStatus);
        commonResult.setMessage("操作成功");
        return commonResult.success(memberLevels);
    }
}
