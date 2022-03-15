package cn.czyx007.SunnyRun.Utils;

import cn.czyx007.SunnyRun.Exception.IMEICodeException;
import cn.czyx007.SunnyRun.Exception.RunningTimeException;
import cn.czyx007.SunnyRun.Run.Run;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author : 张宇轩
 * @createTime : 2022/3/2 - 18:35
 * @lastModifyTime : 2022/3/2 - 19:14
 * @description : 工具类，提供创建所需文件/文件夹，解码数字字符串，从服务器请求资源以获取json格式的字符串的功能
 */
public class RunUtils {
    /**
     * 从RunTime.txt文件中获取跑步时长区间
     * @return
     * @throws Exception
     */
    public static String[] getTimeStr() throws Exception {
        try(BufferedReader brTime = new BufferedReader(new FileReader("RunTime.txt"))) {
            String timeStr = brTime.readLine();//读取跑步时长区间
            if (timeStr == null)
                throw new RunningTimeException("RunningTimeException：未指定跑步随机时长区间！\n请在RunTime.txt文件内以例如'610 690'的方式指定\n(无单引号，以空格分隔)");
            return timeStr.split("\\s+");//以空格分割
        }
    }

    /**
     * 从IMEICode.txt中获取IMEICode
     * @return
     * @throws Exception
     */
    public static String getImeiCode() throws Exception{
        try(BufferedReader br = new BufferedReader(new FileReader("IMEICode.txt"))){
            String ImeiCode = br.readLine();
            if(ImeiCode == null)
                throw new IMEICodeException("IMEICodeException：已创建空白IMEICode.txt文件或已存在的该文件为空，请填入最新的IMEICode");
            return ImeiCode;
        }
    }

    /**
     * 创建IMEICode.txt Log文件夹
     */
    public static void createFileAndDirect() {
        File f = new File("Log");
        File filePrint = new File("Log/" + Run.filename);
        File fileImei = new File("IMEICode.txt");
        File fileTime = new File("RunTime.txt");

        try {
            if (!fileImei.exists())
                fileImei.createNewFile();//创建IMEICode.txt
            if (!fileTime.exists())
                fileTime.createNewFile();//创建RunTime.txt
            if (!f.exists())
                f.mkdir();//创建Log文件夹
            filePrint.createNewFile();//创建日志文件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 对传入的数字字符串解码返回字母字符串以供生成url
     * @param s
     * @return
     */
    public static String encrypt(String s){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            sb.append(Run.table.charAt((int) s.charAt(i) - (int) '0'));
        }
        return sb.toString();
    }

    /**
     * 从服务器请求资源并返回json格式的字符串
     * @param chClient
     * @param url
     * @return
     * @throws IOException
     */
    public static String getJsonString(CloseableHttpClient chClient, String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Version","2.40");

        CloseableHttpResponse response = chClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        String toStringRes = EntityUtils.toString(entity, StandardCharsets.UTF_8);

        EntityUtils.consume(entity);//确保流关闭
        return toStringRes;
    }
}
