package cn.czyx007.sunnyrun;

import cn.czyx007.sunnyrun.utils.RunUtils;
import cn.czyx007.sunnyrun.run.Run;

import java.io.*;

/**
 * @author : 张宇轩
 * @createTime : 2022/3/2 - 16:54
 * @lastModifyTime : 2022/3/2 - 19:14
 * @description : 阳光长跑脚本主类，模拟阳光长跑
 */
public class Main {
    public static void main(String[] args) {
        Run sunnyRun = new Run();
        try {
            String[] time = RunUtils.getTimeStr();
            sunnyRun.run(RunUtils.getImeiCode(), Integer.parseInt(time[0]), Integer.parseInt(time[1]));
        }catch (Exception e){
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("Log/" + Run.filename))) {
                bw.write(e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
