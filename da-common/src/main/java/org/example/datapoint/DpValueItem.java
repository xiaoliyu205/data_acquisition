package org.example.datapoint;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName: DpValueItem
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/22 16:58
 **/
@Data
@AllArgsConstructor
public class DpValueItem {
    private String dpName;
    private String value;
    private Date time;
}
