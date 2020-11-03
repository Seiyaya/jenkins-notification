package xyz.seiyaya.jenkins.plugin.service.impl;

import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.util.DateUtils;
import xyz.seiyaya.jenkins.plugin.bean.DingTalkMessageText;
import xyz.seiyaya.jenkins.plugin.bean.ProjectInfo;
import xyz.seiyaya.jenkins.plugin.help.DingTalkMessageHelper;
import xyz.seiyaya.jenkins.plugin.helper.CacheBean;
import xyz.seiyaya.jenkins.plugin.service.NotificationService;

import java.util.Calendar;
import java.util.Locale;

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
        String date = DateUtils.format(Calendar.getInstance(Locale.CHINA).getTime(),"yyyy-MM-dd HH:mm:ss");
        DingTalkMessageText dingTalk = DingTalkMessageText.builder().msgtype("text")
                .text(DingTalkMessageText.DingTalkMessage.builder().content(String.format("重启 【%s】 环境，时间: %s", env, date)).build())
                .build();

        // 取默认的token和secret  如果发送一次会将页面上的token和secret赋值，这里根据策略来是否取默认值
        int category = CacheBean.getConfigInt(CacheBean.ITEM_CONFIG_GET_CATEGORY,1);
        if (category == 0) {
            if (StringUtils.isBlank(this.accessToken)) {
                this.accessToken = CacheBean.getConfig(CacheBean.ITEM_CONFIG_ACCESS_TOKEN);
            }
            if (StringUtils.isBlank(this.secret)) {
                this.secret = CacheBean.getConfig(CacheBean.ITEM_CONFIG_SECRET);
            }
        } else {
            this.accessToken = CacheBean.getConfig(CacheBean.ITEM_CONFIG_ACCESS_TOKEN);
            this.secret = CacheBean.getConfig(CacheBean.ITEM_CONFIG_SECRET);
        }
        DingTalkMessageHelper.sendTextMessage(accessToken,secret,dingTalk);
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
