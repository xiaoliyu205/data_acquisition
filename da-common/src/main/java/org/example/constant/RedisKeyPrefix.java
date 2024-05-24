package org.example.constant;

import org.springframework.stereotype.Component;

/**
 * @ClassName: RedisKeyPrefix
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/22 17:19
 **/
@Component
public class RedisKeyPrefix {

    public static final String DATA_SOURCE = "DC:";

    public static final String DATA_POINT = DATA_SOURCE + "DP:";

    public static final String ADDRESS_CONFIG = DATA_SOURCE + "ADDR:";
}
