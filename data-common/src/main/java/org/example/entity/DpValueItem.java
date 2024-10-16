package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: DpValueItem
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/30 14:09
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DpValueItem {
    private String dpName;
    private Object value;
}
