package com.roy.exam1.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.enterprise.inject.New;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GetSina {

    /**
     *
     * @Title: saveHtml
     * @Description: 将抓取过来的数据保存到本地或者json文件
     * @param
     * @return void 返回类型
     * @author liangchu
     * @date 2017-12-28 下午12:23:05
     * @throws
     */
//    将抓取出来的信息保存到本地，提高效率
    public static void saveHtml(String url) throws IOException {
        try {
            // 这是将首页的信息存入到一个html文件中 为了后面分析html文件里面的信息做铺垫
            File dest = new File("src/temp/reptile2.html");
            // 接收字节输入流
            InputStream is;
            // 字节输出流
            FileOutputStream fos = new FileOutputStream(dest);
            URL temp = new URL(url);
            // 这个地方需要加入头部 避免大部分网站拒绝访问
            // 这个地方是容易忽略的地方所以要注意
            URLConnection uc = temp.openConnection();
            // 因为现在很大一部分网站都加入了反爬虫机制 这里加入这个头信息
            uc.addRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 " +
                            "(Windows NT 10.0; WOW64) " +
                            "AppleWebKit/537.36 " +
                            "(KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36");

//                    "Mozilla/5.0 "
//                            + "(iPad; U; CPU OS 4_3_3 like Mac OS X; en-us) "
//                            + "AppleWebKit/533.17.9"
//                            + " (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5");
            is = temp.openStream();
            // 为字节输入流加入缓冲
            BufferedInputStream bis = new BufferedInputStream(is);
            // 为字节输出流加入缓冲
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            int length;
            byte[] bytes = new byte[1024 * 20];
            while ((length = bis.read(bytes, 0, bytes.length)) != -1) {
                fos.write(bytes, 0, length);
            }
            bos.close();
            fos.close();
            bis.close();
            is.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
        List<New> news = new ArrayList<New>();
        HttpServletResponse response = null;
        HttpServletRequest request = null;
        int tmp=1;
        // 循环解析所有的html文件
        try {
            for (int i = 0; i < files.length; i++) {

                // 首先先判断是不是文件
                if (files[i].isFile()) {
                    // 获取文件名
                    String filename = files[i].getName();
                    // 开始解析文件

                    Document doc = Jsoup.parse(files[i], "UTF-8");
                    // 获取所有内容 获取新闻内容
                    Elements contents = doc.getElementsByClass("ConsTi");
                    for (Element element : contents) {
                        Elements e1 = element.getElementsByTag("a");
                        for (Element element2 : e1) {
                            // System.out.print(element2.attr("href"));
                            // 根据href获取新闻的详情信息
                            String newText = desGetUrl(element2.attr("href"));
                            // 获取新闻的标题
                            String newTitle = element2.text();
                            exportFile(newTitle, newText);
                            System.out.println("抓取成功。。。"+(tmp));
                            tmp++;

                        }
                    }
                }

            }

            //excelExport(news, response, request);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

//    根据url的信息获取这个url下的新闻详情信息
    /**
     *
     * @Title: desGetUrl
     * @Description: 根据url获取连接地址的详情信息
     * @param @param url 参数
     * @return void 返回类型
     * @author liangchu
     * @date 2017-12-28 下午1:57:45
     * @throws
     */
    public static String desGetUrl(String url) {
        String newText="";
        try {
            Document doc = Jsoup
                    .connect(url)
                    .userAgent(
                            "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; MALC)")
                    .get();
            // System.out.println(doc);
            // 得到html下的所有东西
            //Element content = doc.getElementById("article");
            Elements contents = doc.getElementsByClass("article");
            if(contents != null && contents.size() >0){
                Element content = contents.get(0);
                newText = content.text();
            }
            //System.out.println(content);
            //return newText;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return newText;
    }

//    将新闻信息写入文件中
    /*
     * 将新闻标题和内容写入文件
     */
    public static void exportFile(String title,String content){

        try {
            File file = new File("C:/Users/royl/Desktop/work/study/python/xinwen.txt");

            if (!file.getParentFile().exists()) {//判断路径是否存在，如果不存在，则创建上一级目录文件夹
                file.getParentFile().mkdirs();
            }
            FileWriter fileWriter=new FileWriter(file, true);
            fileWriter.write(title+"----------");
            fileWriter.write(content+"\r\n");
            fileWriter.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

//    主函数
    public static void main(String[] args) {
//        String url = "http://news.sina.com.cn/hotnews/?q_kkhha";
        String url = "http://news.sina.com.cn/hotnews/?q_kkhha";
        try {
            saveHtml(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 解析本地html文件
        getLocalHtml("src/temp");
    }



}
