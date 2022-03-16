package cn.czyx007.sunnyrun.exception;

/**
 * @author : 张宇轩
 * @createTime : 2022/3/2 - 18:07
 * @lastModifyTime : 2022/3/2 - 19:14
 * @description : 未提供长跑随机时长区间的异常
 */
public class RunningTimeException extends Exception{
    private static final long serialVersionUID = 4641115945799592390L;

    public RunningTimeException() {
    }

    public RunningTimeException(String message) {
        super(message);
    }
}
