package org.example.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @ClassName: DpValueWrite
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/30 14:10
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class DpValueWrite extends DpValueItem {

    private String mark;

    public DpValueWrite(String dpName, String value, String mark) {
        super(dpName, value);
        this.mark = mark;
    }
}
