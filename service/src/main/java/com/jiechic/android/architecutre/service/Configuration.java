package com.jiechic.android.architecutre.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by jiechic on 15/10/12.
 */
public class Configuration {
    private Properties config = new Properties();// 记录配置项
    private static Configuration instance=null;

    // 此构造方法用于新建配置文件
    private Configuration() throws IOException {

        FileInputStream fin =null;
        //String filename = "service.properties";// 配置文件名
        String path = this.getClass().getResource("/").getPath()+ "config.properties";
        try{
            fin = new FileInputStream(path);
        }catch(Exception e){

        }
        if(fin==null){
            //从路径字符串中取出工程路径
            String filename=path.substring(1, path.indexOf("classes"))+"classes/config.properties";
            fin = new FileInputStream(filename);
        }
        config.load(fin); // 载入文件
        fin.close();
    }

    // 从指定文件名读入配置信息
    public static Configuration getInstance() {
        if(instance==null){
            try {
                instance=new Configuration();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                instance=null;
            }
        }
        return instance;
    }

    // 指定配置项名称，返回配置值
    public String getValue(String itemName) {
        return config.getProperty(itemName);
    }

    // 指定配置项名称和默认值，返回配置值
    public String getValue(String itemName, String defaultValue) {
        return config.getProperty(itemName, defaultValue);
    }
}
