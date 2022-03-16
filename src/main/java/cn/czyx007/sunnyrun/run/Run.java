package cn.czyx007.sunnyrun.run;

import cn.czyx007.sunnyrun.utils.RunUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author : 张宇轩
 * @createTime : 2022/3/1 - 8:40
 * @lastModifyTime : 2022/3/2 - 19:14
 * @description : 获取用户长跑信息，向服务器提交开始跑步、结束跑步的请求，模拟长跑
 */
public class Run {
    public static StringBuilder table = new StringBuilder();//用于存放随机排序生成的字符串以供生成url
    public static final String filename;//日志文件名

    static {
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
        filename = "Log-"+ft.format(dNow)+".txt";//生成日志文件名
        RunUtils.createFileAndDirect();//创建IMEICode.txt RunTime.txt Log文件夹
    }

    public Run() {
        final String alphaStr = "abcdefghijklmnopqrstuvwxyz";
        List<Character> alphabet = new ArrayList<>(26);
        for (int i = 0; i < alphaStr.length(); i++) {//生成可排序的字母表数组
            alphabet.add(alphaStr.charAt(i));
        }
        Collections.shuffle(alphabet);//随机排序
        for (int i = 0; i < 10; i++) {//生成10个字母的字符串，用于生成url
            table.append(alphabet.get(i));
        }
    }

    public void run(String IMEI,int low,int high) {
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

        try(BufferedWriter bw = new BufferedWriter(new FileWriter("Log/"+filename))) {
            String API_ROOT = "http://client3.aipao.me/api";  // client3 for Android
            String LoginUrl = API_ROOT+"/%7Btoken%7D/QM_Users/Login_AndroidSchool?IMEICode="+IMEI;
            //Login
            String tokenJson = RunUtils.getJsonString(closeableHttpClient, LoginUrl);

            JSONObject loginJsonObject = new JSONObject(tokenJson);
            if (!loginJsonObject.optBoolean("Success")) {
                bw.write("IMEICodeException：当前IMEICode已过期，请在IMEICode.txt文件中替换为最新的IMEICode");
                return;
            }
            JSONObject loginJsonData = loginJsonObject.optJSONObject("Data");
            String token = loginJsonData.optString("Token");

            //Get User Info
            String GSurl = API_ROOT + "/" + token + "/QM_Users/GS";
            String GSjson = RunUtils.getJsonString(closeableHttpClient, GSurl);

            JSONObject GSjsonObject = new JSONObject(GSjson);
            JSONObject GSjsonData = GSjsonObject.optJSONObject("Data");
            JSONObject user = GSjsonData.optJSONObject("User");
            JSONObject schoolRun = GSjsonData.optJSONObject("SchoolRun");
            String Lengths = schoolRun.optString("Lengths");

            bw.write("User Info:" + user.optInt("UserID") + " " + user.optString("NickName")
                    + " " + user.optString("UserName") + " " + user.optString("Sex") + "\n");
            bw.write("Running Info:" + schoolRun.optString("Sex") + " "
                    + schoolRun.optString("SchoolId") + " " + schoolRun.optString("SchoolName") + " "
                    + schoolRun.optString("MinSpeed") + " " + schoolRun.optString("MaxSpeed") + " "
                    + schoolRun.optString("Lengths") + "\n");

            //Start Running
            String SRSurl = API_ROOT + "/" + token + "/QM_Runs/SRS?S1=30.534736&S2=114.367788&S3=" + Lengths;
            String SRSjson = RunUtils.getJsonString(closeableHttpClient, SRSurl);

            JSONObject SRjsonObject = new JSONObject(SRSjson);
            JSONObject SRjsonData = SRjsonObject.optJSONObject("Data");
            String RunId = SRjsonData.optString("RunId");

            Random random = new Random();
            String RunTime = Integer.toString( (random.nextInt(high-low+1)+low) );  // seconds
            String RunDist = Integer.toString( Integer.parseInt(Lengths) + (random.nextInt(4)) );  // meters
            String RunStep = Integer.toString( (random.nextInt(1600-1300+1)+1300) );  // steps

            long StartT = System.currentTimeMillis();
            for (int i = 0; i < Integer.parseInt(RunTime); i++) {
                bw.write(("Current Minutes: " + (i/60) +" Running Progress: " +
                        (new BigDecimal(i*100.0 / Integer.parseInt(RunTime))
                        .setScale(3, RoundingMode.HALF_UP).doubleValue()) + "%\n"));
            }
            bw.write(("\nRunning MillSeconds:"+ (System.currentTimeMillis() - StartT)+"\n"));

            //End Running
            String EndUrl = API_ROOT + "/" + token + "/QM_Runs/ES?S1=" + RunId + "&S4=" +
                    RunUtils.encrypt(RunTime) + "&S5=" + RunUtils.encrypt(RunDist) +
                    "&S6=&S7=1&S8=" + table + "&S9=" + RunUtils.encrypt(RunStep);
            String EndJson = RunUtils.getJsonString(closeableHttpClient, EndUrl);
            JSONObject endJsonObject = new JSONObject(EndJson);

            bw.write("-----------------------\n");
            bw.write(("Time:"+ RunTime + "\n"));
            bw.write(("Distance:"+ RunDist + "\n"));
            bw.write(("Steps:"+ RunStep + "\n"));
            bw.write("-----------------------\n");

            if (endJsonObject.optBoolean("Success")) {
                bw.write(("[+]OK:" + endJsonObject.optString("Data")));
            } else {
                bw.write(("[!]Fail:" + endJsonObject.optString("Data")));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}