package xyz.seiyaya.jenkins.plugin.bean;

import java.util.HashSet;
import java.util.Set;

/**
 * 工程信息
 * @author wangjia
 * @version 1.0
 * @date 2020/10/30 11:38
 */
public class ProjectInfo {

    public static final Set<String> envs = new HashSet<>();

    static {
        envs.add("dev");
        envs.add("test");
        envs.add("uat");
        envs.add("sit");
        envs.add("mars");
        envs.add("pre");
    }

    /**
     * 环境
     */
    private String env;

    /**
     * 上次通知时间
     */
    private Long lastUpdateTime;

    public ProjectInfo(String env, Long lastUpdateTime) {
        this.env = env;
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public Long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
