package org.example.opcua;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.example.constant.RedisKeyPrefix;
import org.example.entity.OpcUaAddrInfo;
import org.example.exception.InvalidDpNameException;
import org.example.exception.WriteFailException;
import org.example.redis.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @ClassName: OpcUaWrite
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/28 10:46
 **/
@Slf4j
@Component
public class OpcUaWrite {

    private final RedisCache redisCache;

    @Autowired
    public OpcUaWrite(RedisCache redisCache) {
        this.redisCache = redisCache;
    }

    public Boolean writeNodeValue(String dpName, Object value) {
        String info = redisCache.get(RedisKeyPrefix.NODE_CONFIG + dpName);
        if (Objects.isNull(info)) {
            throw new InvalidDpNameException("Not found redis key: " + dpName);
        }
        OpcUaAddrInfo opcUaInfo = JSON.parseObject(info, OpcUaAddrInfo.class);
        OpcUaClient client = OpcUaDriver.getOpcUaClient(opcUaInfo.getUrl());
        if (Objects.isNull(client)) {
            throw new InvalidDpNameException("Not found OpcUa client, url is : " + opcUaInfo.getUrl() + dpName);
        }
        NodeId nodeId = new NodeId(opcUaInfo.getNamespaceIndex(), opcUaInfo.getAddress());
        DataValue nowValue = new DataValue(new Variant(value), null, null);
        StatusCode statusCode = client.writeValue(nodeId, nowValue).join();
        if (statusCode.isGood()) {
            return true;
        } else {
            throw new WriteFailException("Write is fail, Status code is : " + statusCode);
        }
    }
}
