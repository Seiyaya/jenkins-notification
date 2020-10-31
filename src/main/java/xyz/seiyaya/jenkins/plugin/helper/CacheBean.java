package xyz.seiyaya.jenkins.plugin.helper;

import org.apache.commons.lang.StringUtils;
import xyz.seiyaya.jenkins.plugin.bean.ProjectInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author wangjia
 * @version 1.0
 * @date 2020/10/30 19:13
 */
public class CacheBean {


    private static final Map<String, ProjectInfo> TIME_MAP = new ConcurrentHashMap<>();
    /**
     * 缓存配置
     */
    private static final Map<String,String> CONFIG_MAP = new HashMap<>();

    public static final String CONFIG_FILE_PATH =  System.getProperty("user.dir").substring(0, System.getProperty("user.dir").indexOf(File.separator)+1) + "/usr/local/jenkins/pmis.properties";

    /**
     * 判断是否是同一次重启间隔时间
     */
    public static final String ITEM_CONFIG_RESTART_TIME = "ding.restart.time";

    /**
     * 钉钉token
     */
    public static final String ITEM_CONFIG_ACCESS_TOKEN= "ding.config.token";

    /**
     * 钉钉密钥
     */
    public static final String ITEM_CONFIG_SECRET = "ding.config.secret";

    static{
        readConfig();
        new ConfigMapUpdateThread().start();
    }

    /**
     * 读取系统配置
     */
    private static void readConfig() {
        // 读取配置
        File file = new File(CONFIG_FILE_PATH);
        if(file.exists()){
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
                Properties properties = new Properties();
                properties.load(inputStream);
                Enumeration<?> enumeration = properties.propertyNames();
                while(enumeration.hasMoreElements()){
                    Object o = enumeration.nextElement();
                    CONFIG_MAP.put(o.toString(),properties.getProperty(o.toString()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if( inputStream != null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public static void putConfig(String key,String value){
        CONFIG_MAP.put(key,value);
    }

    public static String getConfig(String key){
        return getConfig(key,"");
    }

    public static int getConfigInt(String key,Integer defaultValue){
        String config = getConfig(key, defaultValue.toString());
        return Integer.parseInt(config);
    }

    public static String getConfig(String key,String defaultValue){
        String value = CONFIG_MAP.get(key);
        if(StringUtils.isBlank(value)){
            return defaultValue;
        }
        return value;
    }


    public static void putProject(String env,ProjectInfo projectInfo){
        TIME_MAP.putIfAbsent(env , projectInfo);
    }

    public static ProjectInfo getProject(String env){
        return TIME_MAP.get(env);
    }


    public static class ConfigMapUpdateThread extends Thread{

        private long lastModified = 0;

        @Override
        public void run() {
            // 60s检查一次pmis.properties 文件是否有更新
            while(true){
                File file = new File(CONFIG_FILE_PATH);
                long lastModified = file.lastModified();
                if(this.lastModified == 0){
                    // 首次检查文件，不修改配置
                    this.lastModified = lastModified;
                }

                if(this.lastModified != lastModified){
                    // 配置文件和上次的文件不同，重新加载配置
                    readConfig();
                    this.lastModified = lastModified;
                    System.out.println(Thread.currentThread().getId()+" --> "+ this.lastModified + "   重新加载配置:" + CONFIG_MAP.get(ITEM_CONFIG_SECRET));
                }

                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
