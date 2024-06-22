package org.example.controller;

import com.alibaba.fastjson.JSON;
import org.example.entity.DpValueItem;
import org.example.redis.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: LayoutController
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/6/21 0:17
 **/
@RestController
@RequestMapping("/layout")
public class LayoutController {

    @Autowired
    private RedisCache redisCache;

    @GetMapping("/init")
    public Map<String, DpValueItem> getLayoutInit() {
        List<String> values = redisCache.mGet(redisCache.keys("DataPoint:Layout*"));
        return values.stream().map(i-> JSON.parseObject(i, DpValueItem.class)).collect(Collectors.toMap(DpValueItem::getDpName, n->n));
    }
}
