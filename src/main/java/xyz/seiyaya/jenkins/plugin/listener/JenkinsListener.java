package xyz.seiyaya.jenkins.plugin.listener;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.Descriptor;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import hudson.tasks.Publisher;
import xyz.seiyaya.jenkins.plugin.notify.Notification;

import java.util.Map;

/**
 * 监听器，触发对应的事件
 * @author wangjia
 * @version 1.0
 * @date 2020/10/30 10:55
 */
@Extension
public class JenkinsListener extends RunListener<AbstractBuild> {

    @Override
    public void onStarted(AbstractBuild abstractBuild, TaskListener listener) {
        // 开始构建的时候调用
        Map<Descriptor<Publisher>, Publisher> map = abstractBuild.getProject().getPublishersList().toMap();
        for (Publisher publisher : map.values()) {
            if (publisher instanceof Notification) {
                Notification notification = (Notification) publisher;
                if(notification.isOnStart()){
                    notification.getService(abstractBuild,listener).start();
                }
            }
        }
    }
}
