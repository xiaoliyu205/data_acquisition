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

    public static final String DATA_CONFIG = "DataConfig:";

    public static final String ADDRESS_CONFIG = DATA_CONFIG + "Address:";

    public static final String NODE_CONFIG = DATA_CONFIG + "NodeId:";

    public static final String DATA_POINT = "DataPoint:";

    public static final String DELETE_STR = "Data";
}
