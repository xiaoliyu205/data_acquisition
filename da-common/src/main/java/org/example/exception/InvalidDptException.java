package org.example.exception;

/**
 * @ClassName: InvalidDptException
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/23 16:55
 **/
public class InvalidDptException extends RuntimeException {

    public InvalidDptException(String message) {
        super(message);
    }


    public InvalidDptException(Throwable cause) {
        super(cause);
    }
}
