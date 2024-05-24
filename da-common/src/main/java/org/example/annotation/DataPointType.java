package org.example.annotation;

import java.lang.annotation.*;

/**
 * @ClassName: DataPointType
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/22 15:51
 **/
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataPointType {
    String value();
}
