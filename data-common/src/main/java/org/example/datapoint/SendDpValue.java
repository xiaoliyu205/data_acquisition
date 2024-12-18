package org.example.datapoint;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.example.constant.RedisKeyPrefix;
import org.example.entity.DpValueItem;
import org.example.redis.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: SendDpValue
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/23 15:57
 **/
@Component
@Slf4j
public abstract class SendDpValue {

    private final RedisCache redisCache;

    @Autowired
    public SendDpValue(RedisCache redisCache) {
        this.redisCache = redisCache;
    }

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            10,
            50,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(10),
            new ThreadPoolExecutor.CallerRunsPolicy());

    public void execute(DpValueItem dpValueItem) {
        CompletableFuture.runAsync(() -> {
            Boolean success = redisCache.setIfAbsent(
                    String.format(RedisKeyPrefix.LOCK + dpValueItem.getDpName() + '-' + dpValueItem.getTime().getTime()),
                    Thread.currentThread().getName(),
                    5,
                    TimeUnit.SECONDS
            );
            if (Boolean.TRUE.equals(success)) {
                redisCache.set(RedisKeyPrefix.DATA_POINT + dpValueItem.getDpName(), JSON.toJSONString(dpValueItem, SerializerFeature.WriteDateUseDateFormat));
                log.info("...OpcUa Received and save {}", dpValueItem);
                send(dpValueItem);
                log.info("...OpcUa Received and send {}", dpValueItem);
            } else {
                log.info("...OpcUa Duplicate Messages {}", dpValueItem);
            }
        }, executor);
    }

    protected abstract void send(DpValueItem dpValueItem);
}
