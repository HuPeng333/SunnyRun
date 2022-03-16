package cn.czyx007.sunnyrun.exception;

/**
 * @author : 张宇轩
 * @createTime : 2022/3/2 - 15:34
 * @lastModifyTime : 2022/3/2 - 19:14
 * @description : IMEICode过期或不存在的异常
 */
public class IMEICodeException extends Exception{
    private static final long serialVersionUID = 5713242844166353752L;

    public IMEICodeException() {
    }

    public IMEICodeException(String message) {
        super(message);
    }
}
