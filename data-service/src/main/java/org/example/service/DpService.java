package org.example.service;

/**
 * @author: xiaoliyu
 * @description: TODO
 * @dateTime: 2024/10/17 14:19
 **/
public interface DpService {

    Object dpGet(String dpName);

    Boolean dpSet(String dpName, Object dpValue);

    Boolean dpIsExist(String dpName);
}
