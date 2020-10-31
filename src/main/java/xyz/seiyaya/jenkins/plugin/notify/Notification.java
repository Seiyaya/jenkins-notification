package xyz.seiyaya.jenkins.plugin.notify;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import xyz.seiyaya.jenkins.plugin.helper.CacheBean;
import xyz.seiyaya.jenkins.plugin.service.NotificationService;
import xyz.seiyaya.jenkins.plugin.service.impl.DingTalkNotificationServiceImpl;

import static xyz.seiyaya.jenkins.plugin.helper.CacheBean.ITEM_CONFIG_ACCESS_TOKEN;
import static xyz.seiyaya.jenkins.plugin.helper.CacheBean.ITEM_CONFIG_SECRET;

/**
 * @author wangjia
 * @version 1.0
 * @date 2020/10/30 10:52
 */
public class Notification extends Notifier {

    static{
        new CacheBean();
    }

    /**
     * 钉钉token
     */
    private String accessToken;

    /**
     * 钉钉秘钥
     */
    private String secret;

    private boolean onStart;

    private boolean onSuccess;

    private boolean onFailed;

    private boolean onAbort;

    private NotificationService notificationService;

    public String getAccessToken() {
        return accessToken;
    }

    public String getSecret() {
        return secret;
    }

    public boolean isOnStart() {
        return onStart;
    }

    public boolean isOnSuccess() {
        return onSuccess;
    }

    public boolean isOnFailed() {
        return onFailed;
    }

    public boolean isOnAbort() {
        return onAbort;
    }

    @DataBoundConstructor
    public Notification(String accessToken, String secret, boolean onStart, boolean onSuccess, boolean onFailed, boolean onAbort) {
        super();
        this.accessToken = accessToken;
        this.secret = secret;
        this.onStart = onStart;
        this.onSuccess = onSuccess;
        this.onFailed = onFailed;
        this.onAbort = onAbort;

        if(StringUtils.isBlank(accessToken)){
            this.accessToken = CacheBean.getConfig(ITEM_CONFIG_ACCESS_TOKEN);
        }

        if(StringUtils.isBlank(secret)){
            this.secret = CacheBean.getConfig(ITEM_CONFIG_SECRET);
        }
    }


    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    public NotificationService getService(AbstractBuild abstractBuild, TaskListener listener) {
        if(notificationService == null){
            synchronized (Notification.class){
                if(notificationService == null){
                    notificationService = new DingTalkNotificationServiceImpl(accessToken,secret,listener,abstractBuild);
                }
            }
        }
        return notificationService;
    }


    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
        return true;
    }

    @Override
    public DingTalkDescriptor getDescriptor() {
        return (DingTalkDescriptor) super.getDescriptor();
    }

    @Extension
    public static class DingTalkDescriptor extends BuildStepDescriptor<Publisher> {

        /**
         * builder在这个project是否可用
         * @param aClass
         * @return
         */
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "钉钉通知";
        }

    }
}
