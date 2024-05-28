package org.example.datapoint;

import com.alibaba.fastjson.annotation.JSONField;
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
    @JSONField(format = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date time;

    public DpValueItem(String dpName, String value) {
        this.dpName = dpName;
        this.value = value;
    }
}
