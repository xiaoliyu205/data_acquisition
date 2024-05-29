package org.example.exception;

/**
 * @ClassName: WriteFailException
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/29 16:56
 **/
public class WriteFailException extends RuntimeException {
    public WriteFailException(String message) {
        super(message);
    }

    public WriteFailException(Throwable cause) {
        super(cause);
    }
}
