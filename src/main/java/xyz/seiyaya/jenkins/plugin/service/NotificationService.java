package xyz.seiyaya.jenkins.plugin.service;

/**
 * 通知接口
 * @author wangjia
 * @version 1.0
 * @date 2020/10/30 10:58
 */
public interface NotificationService {

    /**
     * 构建开始通知
     */
    void start();

    /**
     * 构建成功通知
     */
    void success();

    /**
     * 构建失败通知
     */
    void fail();

    /**
     * 停止构建通知
     */
    void abort();
}
