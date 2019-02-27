package com.roy.exam1.utils;

import com.google.gson.Gson;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Exam3 {


    private static String loginURL = "https://www9.qa.ehealthinsurance.com/newbologin/api/login";
//    private static String redirectURL = "https://www9.qa.ehealthinsurance.com/ehi/MainMenu.ds?mcei.app.terminalID=__tid__1_";
//    private static String searchURL = "https://www9.qa.ehealthinsurance.com/bov2/index.html#/appsearchresult?from=individual&t=14595";
    private static String searchURL2 = "https://www9.qa.ehealthinsurance.com/newboapp/api/application/search/individual?pageSize=100&pageStart=1";
    private static String searchURL3 = "https://www9.qa.ehealthinsurance.com/ehi/KeyInfo.ds?appID=12186637&bo.fromScreen=searchResult&mcei.app.terminalID=__tid__1_";
//    private static String searchURL3 = "https://www9.qa.ehealthinsurance.com/ehi/KeyInfo.ds?appID=15312974&bo.fromScreen=searchResult&mcei.app.terminalID=__tid__1_";
    private static String cookie = "";
    private static String x_requested_by = "";
    private static String password = "Ly.20181119";
    static String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36";

    private static DefaultHttpClient httpclient = new DefaultHttpClient();

    //登录入口
    private static boolean login(String url, String userName, String password) {
        //设置登录请求参数
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("userName", userName);
        map.put("password", password);
        map.put("productLine", "IFP");
        Gson gson = new Gson();
        String params = gson.toJson(map);

        HttpClient httpClient = null;
        HttpPost postMethod = null;
        HttpResponse response = null;
        try {
            httpClient = HttpClients.createDefault();
            postMethod = new HttpPost(url);//传入URL地址
            //设置请求头
            postMethod.addHeader("Content-type", "application/json; charset=utf-8");
            postMethod.addHeader("X-Authorization", "AAAA");//设置请求头
            //传入请求参数
            postMethod.setEntity(new StringEntity(params, Charset.forName("UTF-8")));
            response = httpClient.execute(postMethod);//获取响应
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("HTTP Status Code:" + statusCode);
            if (statusCode != HttpStatus.SC_OK) {
                System.out.println("HTTP请求未成功！HTTP Status Code:" + response.getStatusLine());
                return false;
            }
            HttpEntity httpEntity = response.getEntity();
            String reponseContent = EntityUtils.toString(httpEntity);// post请求成功后的返回值
            String firstCookie = response.getFirstHeader("Set-Cookie").getValue();
            String lastCookie = response.getLastHeader("Set-Cookie").getValue(); // 获取cookie值
            cookie = lastCookie + ";" + firstCookie;

            String jessionid = lastCookie.split(";")[0].split("=")[1];

            x_requested_by = Base64
                    .getEncoder()
                    .encodeToString( jessionid.getBytes( StandardCharsets.UTF_8 ) );
            EntityUtils.consume(httpEntity);//释放资源
//            System.out.println("响应内容：" + reponseContent);
//            System.out.println("cookie:"+cookie);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 请求处理方法
     * @param method 方法类型： post , get
     * @param url  处理地址
     * @param params  请求参数  （json类型的String）
     * @return boolean
     */
    private static String doSomething(String method, String url, String params){
        HttpClient httpClient = null;
        HttpResponse response = null;
        String reponseContent = "";
        if(method != null && "post".equals(method)){        //  post请求
            HttpPost postMethod = null;
            try {
                httpClient = HttpClients.createDefault();
                postMethod = new HttpPost(url);//传入URL地址
                //设置请求头
                postMethod.addHeader("Content-type", "application/json; charset=utf-8");
                postMethod.setHeader("Cookie", cookie); // 设置登录成功后获取到的cookie
                postMethod.setHeader("User-Agent", userAgent);
                postMethod.setHeader("X-Requested-By", x_requested_by);
                //传入请求参数
                if(params != null) {
                    postMethod.setEntity(new StringEntity(params, Charset.forName("UTF-8")));
                }
                response = httpClient.execute(postMethod);//获取响应
                if(response != null) {
                    HttpEntity httpEntity = response.getEntity();
                    reponseContent = EntityUtils.toString(httpEntity);// post请求成功后的返回值
                    EntityUtils.consume(httpEntity);//释放资源
                }
                postMethod.abort();//终止请求
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(method != null && "get".equals(method)){       //  get请求

            HttpGet getMethod = new HttpGet(url);
            try {
                getMethod.setHeader("Cookie", cookie); // 设置登录成功后获取到的cookie
                getMethod.setHeader("Content-Type", "application/json;charset=UTF-8");
                getMethod.setHeader("User-Agent", userAgent);
                response = httpclient.execute(getMethod);
                if(response!=null){
                    HttpEntity entity = response.getEntity();
                    reponseContent = EntityUtils.toString(entity,"UTF-8");// get请求成功后的返回值
                    EntityUtils.consume(entity);//释放资源
                }
                getMethod.abort();//终止请求
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("HTTP Status Code:" + statusCode);
        if (statusCode != HttpStatus.SC_OK) {
            System.out.println("HTTP请求未成功！HTTP Status Code:" + response.getStatusLine());
        }
        System.out.println("响应内容：" + reponseContent);
        return reponseContent;
    }


    public static void printText(String username){
        /*如果登录成功了，爬取响应后的html*/
        if (login(loginURL,username,password)) {

            //设置请求参数
            Map<String,String> map = new HashMap<String, String>();
            map.put("firstName", "AAFN-A");
            map.put("fullName", "AAFN-A AALN-A");
            map.put("lastName", "AALN-A");
            Gson gson = new Gson();
            String params = gson.toJson(map);

            System.out.println(params);
            String searchResult = doSomething("post",searchURL2,params);
            System.out.println("searchResult:"+searchResult);
            String result = doSomething("get", searchURL3, null);

        //strong正则表达式
            Pattern p = Pattern.compile("<strong[^>]*>([\\w\\W]*?)<\\/strong\\s*>");
            Matcher m = p.matcher(result);
        //去除strong标签
            result = m.replaceAll("");

            FileWriter writer;
            try {
                writer = new FileWriter("src/temp/reptile2.html");
                writer.write(result);
                writer.flush();
                writer.close();
//                解析本地的html文件获取对应的数据
                getLocalHtml("src/temp");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            System.out.println("Login Default");
        }
    }

    /*
     * 解析本地的html文件获取对应的数据
     */
    public static void getLocalHtml(String path) {
        // 读取本地的html文件
        File file = new File(path);
        // 获取这个路径下的所有html文件
        File[] files = file.listFiles();
        // 循环解析所有的html文件
        try {
            for (int i = 0; i < files.length; i++) {

                // 首先先判断是不是文件
                if (files[i].isFile()) {
                    // 获取文件名
                    String filename = files[i].getName();
                    System.out.println("解析文件名称："+ filename);
                    // 开始解析文件

                    Document doc = Jsoup.parse(files[i], "UTF-8");
                    // 获取内容
                    //获取姓名与生日
                    Elements getNameAndBirthday = doc.select("table[id=innerMembersTable]")
                                                      .select("tr").get(2)
                                                      .select("td");
                    String name = getNameAndBirthday.get(2).text();
                    String birthday = getNameAndBirthday.get(4).text();

                    Element getApplicationStatus = doc.select("table[id=innerAppInfoTable]")
                                                        .select("tr").get(3)
                                                        .select("td").get(5);
                    String applicationStatus = getApplicationStatus.text();

                    Element getEffectiveDate = doc.select("table[id=innerPoliciesTable]")
                                                    .select("tr").get(3)
                                                    .select("td").get(2);
                    String effectiveDate = getEffectiveDate.text();

                    System.out.println("-------------抓取结果：-------------");

                    System.out.println("Applicant Name:" + name);
                    System.out.println("Date of Birth:" + birthday);
                    System.out.println("Application Status:" + applicationStatus);
                    System.out.println("Application Effective Date:" + effectiveDate);

                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("请输入用户名：");
        String name = s.nextLine();
        printText(name);

    }


}
