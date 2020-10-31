package xyz.seiyaya.jenkins.plugin.service.impl;

import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.util.DateUtils;
import xyz.seiyaya.jenkins.plugin.bean.ProjectInfo;
import xyz.seiyaya.jenkins.plugin.helper.CacheBean;
import xyz.seiyaya.jenkins.plugin.helper.HttpHelper;
import xyz.seiyaya.jenkins.plugin.service.NotificationService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.util.Date;

import static xyz.seiyaya.jenkins.plugin.helper.CacheBean.ITEM_CONFIG_RESTART_TIME;

/**
 * 钉钉通知服务类
 * @author wangjia
 * @version 1.0
 * @date 2020/10/30 10:58
 */
public class DingTalkNotificationServiceImpl implements NotificationService {

    /**
     * 钉钉token
     */
    private String accessToken;

    /**
     * 钉钉秘钥
     */
    private String secret;

    private TaskListener listener;

    private AbstractBuild abstractBuild;

    public DingTalkNotificationServiceImpl(String accessToken,String secret, TaskListener listener, AbstractBuild abstractBuild) {
        this.accessToken = accessToken;
        this.secret = secret;
        this.listener = listener;
        this.abstractBuild = abstractBuild;
    }

    @Override
    public void start() {
        // 两分钟内再次触发不生效，可能存在多个服务的重启导致一直发消息
        String displayName = abstractBuild.getProject().getDisplayName();
        if(displayName == null){
            return ;
        }
        String[] split = displayName.split("-");
        if(split.length <= 2 || !ProjectInfo.envs.contains(split[0])){
            return ;
        }
        if(!("restart".equals(split[1]) || "partner".equals(split[1]))){
            // 暂时只支持重启推送
            return;
        }
        String env = split[0];
        if("pre".equals(env)){
            env = "uat";
        }
        ProjectInfo projectInfo = CacheBean.getProject(env);
        if(projectInfo != null){
            long lastUpdateTime = projectInfo.getLastUpdateTime();
            long dateDiff = (System.currentTimeMillis() - lastUpdateTime)/1000;
            int time  = CacheBean.getConfigInt(ITEM_CONFIG_RESTART_TIME,120);
            if( dateDiff > time){
                projectInfo.setLastUpdateTime(System.currentTimeMillis());
            }else{
                return;
            }
        }else{
            projectInfo = new ProjectInfo(env,System.currentTimeMillis());
            CacheBean.putProject(env,projectInfo);
        }

        // 钉钉通知消息
        String date = DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss");
        String msgContent = String.format("重启 【%s】 环境，时间: %s",env,date);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msgtype", "text");
        JSONObject innerObject = new JSONObject();
        innerObject.put("content", msgContent);
        jsonObject.put("text", innerObject);

        // 取默认的token和secret
        if(StringUtils.isBlank(this.accessToken)){
            this.accessToken = CacheBean.getConfig(CacheBean.ITEM_CONFIG_ACCESS_TOKEN);
        }
        if(StringUtils.isBlank(this.secret)){
            this.secret = CacheBean.getConfig(CacheBean.ITEM_CONFIG_SECRET);
        }

        String prefixUrl = String.format("https://oapi.dingtalk.com/robot/send?access_token=%s", this.accessToken);
        try {
            long current = System.currentTimeMillis();
            String sign = getSign(System.currentTimeMillis());
            String s = HttpHelper.sendPostJson(String.format("%s&sign=%s&timestamp=%s", prefixUrl, sign,current), jsonObject.toString());
            listener.getLogger().println("ding talk response:"+s);
        }catch (Exception e){
            System.out.println("消息发送失败");
        }
    }

    public String getSign(Long timestamp) throws Exception {
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)),"UTF-8");
        return sign;
    }

    @Override
    public void success() {

    }

    @Override
    public void fail() {

    }

    @Override
    public void abort() {

    }
}
