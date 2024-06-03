package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @ClassName: OaAddrInfo
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/28 10:45
 **/
@Data
@AllArgsConstructor
public class OpcUaAddrInfo {
    private String url;
    private String address;
    private short namespaceIndex;
}
