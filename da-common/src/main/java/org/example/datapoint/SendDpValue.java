package org.example.datapoint;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.example.constant.RedisKeyPrefix;
import org.example.redis.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName: SendDpValue
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/23 15:57
 **/
@Component
public abstract class SendDpValue {

    @Autowired
    private RedisCache redisCache;

    public void execute(DpValueItem dpValueItem) {
        String dpValueItemStr = JSON.toJSONString(dpValueItem, SerializerFeature.WriteDateUseDateFormat);
        redisCache.set(RedisKeyPrefix.DATA_POINT + dpValueItem.getDpName(), dpValueItemStr);
        send(dpValueItem);
    }

    protected abstract void send(DpValueItem dpValueItem);
}
