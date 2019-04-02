package com.atguigu.gmall.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.io.Serializable;
import java.util.HashMap;

public class PageToMap implements Serializable {
    public static HashMap<String,Object> toMap(Page page){
        HashMap<String, Object> map = new HashMap<>();
        map.put("total",page.getTotal());
        map.put("totalPage",page.getPages());
        map.put("pageSize",page.getSize());
        map.put("list",page.getRecords());
        return map;
    }
}
