package org.example.datapoint;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.example.constant.RedisKeyPrefix;
import org.example.redis.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * @ClassName: SendDpValue
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/23 15:57
 **/
@Component
@Slf4j
public abstract class SendDpValue {

    @Autowired
    private RedisCache redisCache;

    public void execute(DpValueItem dpValueItem) {
        CompletableFuture.runAsync(() -> {
            redisCache.set(RedisKeyPrefix.DATA_POINT + dpValueItem.getDpName(), JSON.toJSONString(dpValueItem, SerializerFeature.WriteDateUseDateFormat));
            log.info("...OpcUa Received and save {}", dpValueItem);
            send(dpValueItem);
            log.info("...OpcUa Received and send {}", dpValueItem);
        });
    }

    protected abstract void send(DpValueItem dpValueItem);
}
