package org.example.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @ClassName: DpValueRead
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/30 14:10
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class DpValueRead extends DpValueItem {

    @JSONField(format = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date time;

    public DpValueRead(String dpName, Object value, Date time) {
        super(dpName, value);
        this.time = time;
    }
}
