package org.example.exception;

/**
 * @ClassName: InvalidDptException
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/23 16:55
 **/
public class InvalidException extends RuntimeException {

    public InvalidException(String message) {
        super(message);
    }

    public InvalidException(Throwable cause) {
        super(cause);
    }
}
