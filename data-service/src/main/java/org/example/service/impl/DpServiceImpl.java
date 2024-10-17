package org.example.service.impl;

import com.alibaba.fastjson.JSON;
import org.example.constant.RedisKeyPrefix;
import org.example.entity.DpValueItem;
import org.example.opcua.OpcUaWrite;
import org.example.redis.RedisCache;
import org.example.service.DpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: xiaoliyu
 * @description: TODO
 * @dateTime: 2024/10/17 14:20
 **/
@Service
public class DpServiceImpl implements DpService {

    private final RedisCache redisCache;
    private final OpcUaWrite opcUaWrite;

    @Autowired
    public DpServiceImpl(RedisCache redisCache, OpcUaWrite opcUaWrite) {
        this.redisCache = redisCache;
        this.opcUaWrite = opcUaWrite;
    }

    @Override
    public Object dpGet(String dpName) {
        DpValueItem dpValueItem = JSON.parseObject(redisCache.get(RedisKeyPrefix.DATA_POINT + dpName), DpValueItem.class);
        return dpValueItem.getValue();
    }

    @Override
    public Boolean dpSet(String dpName, Object dpValue) {
        return opcUaWrite.writeNodeValue(dpName, dpValue);
    }

    @Override
    public Boolean dpIsExist(String dpName) {
        return redisCache.exists(RedisKeyPrefix.DATA_POINT + dpName);
    }
}
