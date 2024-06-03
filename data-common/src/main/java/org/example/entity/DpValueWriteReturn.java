package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: DpValueWriteReturn
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/30 14:21
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DpValueWriteReturn {
    private String mark;
    private Boolean isGood;
    private String err;
}
