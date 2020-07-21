package com.study.hello.spring.boot;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.hello.spring.boot.entity.User;
import com.study.hello.spring.boot.mapper.UserMapper;
import com.sun.jndi.toolkit.url.Uri;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(classes = HelloSpringBootApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Transactional
//@Rollback
class HelloSpringBootApplicationTests {

    @LocalServerPort
    private int port;

    private URL base;

    @Autowired
    private TestRestTemplate template;

    @BeforeEach
    public void setUp() throws Exception{
        this.base = new URL("http://localhost:"+ port +"/boot");
    }

    @Test
    void contextLoads() {
        ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Autowired
    private UserMapper userMapper;

    @Test
    public void add(){
        User user = new User();
        user.setName("周瑾芯");
        user.setAge(0);
        user.setAddress("江苏省淮安市淮阴区");
        userMapper.insert(user);
    }

    @Test
    public void select(){
        List<User> usersList = userMapper.selectAll();
        double i = 0.0;
        while (true){
            i = Math.random() * 10;
            if(i > usersList.size()){
                System.out.printf("当前值为："+ i +" try again!");
                continue;
            }
            if(i < usersList.size()){
                break;
            }
        }

        User user = usersList.get(new Double(i).intValue());
        System.out.println(user.getName());
    }

    @Test
    public void selectPage(){
        PageHelper.startPage(1,2);
        Example example = new Example(User.class);
        PageInfo<User> pageInfo = new PageInfo<>(userMapper.selectByExample(example));

        List<User> list = pageInfo.getList();
        for(User user : list){
            System.out.println(user.getName());
        }
    }

    @Test
    public void getPicInfo(){
        String filePath = "D:/Flag1/";

        Map<String, Object> map = new HashMap();
        map.put("flag1","true");

        List<Map<String, Object>> list = userMapper.getPicInfo(map);
        System.out.println(list.size());

        int cnt = 0;
        for(Map<String, Object> objectMap: list){
            String fileName = objectMap.get("ADD_USER_NAME").toString() + "-" + objectMap.get("TYPES") + "-" + objectMap.get("GOODS_CODE")+".jpg";
            String fileUrl = objectMap.get("URL").toString();
            uploadPic(filePath, fileName, fileUrl, cnt);
            cnt ++;
        }
    }

    public static void uploadPic(String filePath, String fileName, String fileUrl, int cnt){
        CloseableHttpClient client = HttpClientBuilder.create().build();
        URI uri = null;
        try {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("path", fileUrl));
            uri = new URIBuilder().setScheme("http").setHost("221.226.214.210")
                    .setPort(8115).setPath("/file/attachment/download.html")
                    .setParameters(params).build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        httpGet.setHeader("Accept-Encoding","gzip, deflate");
        httpGet.setHeader("Accept-Language","zh-CN,zh;q=0.9,en;q=0.8");
        httpGet.setHeader("Connection","keep-alive");
        httpGet.setHeader("Host","221.226.214.210:8115");
        httpGet.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.116 Safari/537.36");
        httpGet.setHeader("Upgrade-Insecure-Requests","1");
        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpGet);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            System.out.println("第"+ cnt +"次请求响应状态为:" + response.getStatusLine());
            System.out.println("请求URL:" + httpGet.getURI());
            if (responseEntity != null) {
                InputStream inputStream = responseEntity.getContent();
                savePicToDisk(inputStream, filePath, cnt+fileName);
            }else{
                System.out.println("无法获取附件！");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void savePicToDisk(InputStream in, String dirPath, String filePath) {
        try {
            File dir = new File(dirPath);
            if (dir == null || !dir.exists()) {
                dir.mkdirs();
            }
            //文件真实路径
            String realPath = dirPath.concat(filePath);
            File file = new File(realPath);
            if (file == null || !file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = in.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
