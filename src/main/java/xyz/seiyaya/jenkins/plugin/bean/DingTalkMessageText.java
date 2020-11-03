package xyz.seiyaya.jenkins.plugin.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 钉钉文本消息
 * @author wangjia
 * @version 1.0
 * @date 2020/11/3 14:11
 */
@Data
@Builder
@AllArgsConstructor
@SuppressWarnings("all")
public class DingTalkMessageText implements Serializable {

    private String msgtype = "text";

    private DingTalkMessageAt at;

    private DingTalkMessage text;

    @AllArgsConstructor
    @Builder
    public static class DingTalkMessage implements Serializable{
        private String content;
    }

}
