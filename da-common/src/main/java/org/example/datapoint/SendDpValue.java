package org.example.datapoint;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.example.constant.RedisKeyPrefix;
import org.example.entity.DpValueRead;
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

    public void execute(DpValueRead dpValueRead) {
        CompletableFuture.runAsync(() -> {
            redisCache.set(RedisKeyPrefix.DATA_POINT + dpValueRead.getDpName(), JSON.toJSONString(dpValueRead, SerializerFeature.WriteDateUseDateFormat));
            log.info("...OpcUa Received and save {}", dpValueRead);
            send(dpValueRead);
            log.info("...OpcUa Received and send {}", dpValueRead);
        });
    }

    protected abstract void send(DpValueRead dpValueRead);
}
