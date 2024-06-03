package org.example.annotation;

import java.lang.annotation.*;

/**
 * @ClassName: SendDpItemType
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/23 16:09
 **/
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SendItemType {
    String value();
}
