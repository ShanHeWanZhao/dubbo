package org.apache.dubbo.demo.provider;

/**
 * @author tanrd1
 * @date 2022/08/09 14:39
 */
public class MyException extends RuntimeException{
    public MyException() {
    }

    public MyException(String message) {
        super(message);
    }

    public MyException(String message, Throwable cause) {
        super(message, cause);
    }
}
