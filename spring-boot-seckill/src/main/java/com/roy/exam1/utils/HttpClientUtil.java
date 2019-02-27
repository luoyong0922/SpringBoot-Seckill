package com.roy.exam1.utils;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class HttpClientUtil {
    /**
     *
     * @Title: doPost
     * @Description: 模仿提交post请求
     * @param @param url
     * @param @param map 请求的参数 采用map集合封装参数
     * @param @param charset 编码格式
     * @param @return    参数
     * @return String 返回类型
     * @author  liangchu
     * @date 2017-12-31 下午7:09:14
     * @throws
     */
    public static String doPost(String url, Map<String,Object> map, String charset){
        HttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try{
            httpClient = new SSLClient();
            httpPost = new HttpPost(url);
            //设置参数
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            Iterator iterator = map.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<String,Object> elem = (Map.Entry<String, Object>) iterator.next();
                list.add(new BasicNameValuePair(elem.getKey(),(String) elem.getValue()));
            }
            if(list.size() > 0){
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,charset);
                httpPost.setEntity(entity);
            }
            HttpResponse response = httpClient.execute(httpPost);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null){
                    result = EntityUtils.toString(resEntity,charset);
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return result;
    }



//根据url链接地址获取对应的信息列表
    /**
     *
     * @Title: spiderZH2
     * @Description: 这里是采用httpclient包发送请求 获取需要加载的列表
     * @param @param url    参数url地址 offset 根据offset显示问题信息列表
     * @return void 返回类型
     * @author  liangchu
     * @date 2017-12-31 下午2:11:23
     * @throws
     */
    public static void spiderZH2(String url,int offset){
        try {
            //String curl ="https://www.zhihu.com/node/ExploreRecommendListV2";
            Map<String,Object> createMap = new HashMap<String,Object>();
            String charset = "utf-8";
            // method 提交的参数
            createMap.put("method", "next");
            Map<String,Object> map = new HashMap<String, Object>();
            // 分页显示的数据
            map.put("limit", 20);
            map.put("offset", offset);
            createMap.put("method", "next");
            Gson gson = new Gson();
            String mapStr = gson.toJson(map);
            // 请求的参数
            createMap.put("params", mapStr);
            // 根据httpclient模仿post请求
            String httpOrgCreateTestRtn = HttpClientUtil.doPost(url,createMap,charset);
            System.out.println("1========"+httpOrgCreateTestRtn);
            Map maps = gson.fromJson(httpOrgCreateTestRtn, Map.class);
            String html = maps.get("msg").toString();
            System.out.println("2========"+html);
            Document doc = Jsoup.parse(html);
            Elements elements =
                    doc.select("div[class=zm-item]").select("h2").
                            select("a[class=question_link]");
            File file = new File("C:/zhifuwenda.txt");
            // 遍历每个问题节点
            for (Element question : elements) {
                // 获取连接地址
                String qUrl = question.attr("href");
                // 这里需要判断urlhttp格式
                if(!qUrl.contains("https://")){
                    qUrl = "https://www.zhihu.com"+qUrl;
                }
                Document document2=Jsoup.connect(qUrl)
                        .userAgent("Mozilla/5.0 "
                                + "(iPad; U; CPU OS 4_3_3 like Mac OS X; en-us) "
                                + "AppleWebKit/533.17.9"
                                + " (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5")
                        .get();
                // 问题标题
                Elements title = document2.select("#root").select("div").select("main").
                        select("div").select("div:nth-child(10)").select("div.QuestionHeader").
                        select("div.QuestionHeader-content").select("div.QuestionHeader-main").
                        select("h1");
                // 回答问题的内容
                Elements content = document2.select("#root").select("div").select("main").
                        select("div").select("div.Question-main").select("div.Question-mainColumn").
                        select("div.Card.AnswerCard").select("div").select("div").
                        select("div.RichContent.RichContent--unescapable").
                        select("div.RichContent-inner");
                if (!file.getParentFile().exists()) {//判断路径是否存在，如果不存在，则创建上一级目录文件夹
                    file.getParentFile().mkdirs();
                }
                FileWriter fileWriter=new FileWriter(file, true);
                fileWriter.write("=============链接:"+qUrl+"\r\n");
                fileWriter.write("=============标题:"+title.get(0).text()+"\r\n");
                fileWriter.write("=============回答:"+content.get(0).text()+"\r\n");
                fileWriter.close();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String [] args){
        // 这里采用循环的方式去除列表
        String url = "https://www.zhihu.com/node/ExploreRecommendListV2";
        for(int i=1;i<1000;i++){
            spiderZH2(url,59+i*20);
        }
    }

}