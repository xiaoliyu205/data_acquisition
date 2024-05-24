package org.example.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @ClassName: OpcUaAddress
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/22 17:37
 **/
@Data
@TableName("opcua_address")
public class OpcUaAddress {

    @TableField(value = "dpt")
    private String dataPointType;

    @TableField(value = "datapoint")
    private String dataPoint;

    @TableField(value = "plc_name")
    private String plcName;

    @TableField(value = "opcua_address")
    private String address;

    @TableField(value = "url")
    private String url;

    @TableField(value = "namespace")
    private Integer namespaceIndex;

    @TableField(value = "user_name")
    private String userName;

    @TableField(value = "password")
    private String password;
}
