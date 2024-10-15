package org.example.exception;

/**
 * @ClassName: InvalidDpNameException
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/29 16:40
 **/
public class InvalidDpNameException extends RuntimeException {

    public InvalidDpNameException(String message) {
        super(message);
    }

    public InvalidDpNameException(Throwable cause) {
        super(cause);
    }
}
