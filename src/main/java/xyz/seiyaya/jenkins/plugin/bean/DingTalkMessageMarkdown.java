package xyz.seiyaya.jenkins.plugin.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * markdown西消息
 * @author wangjia
 * @version 1.0
 * @date 2020/11/3 14:21
 */
@Data
@SuppressWarnings("all")
public class DingTalkMessageMarkdown implements Serializable {

    private String msgtype = "markdown";
    private DingTalkMessageAt at;
    private DingTalkMessage markdown;

    @Data
    public static class DingTalkMessage implements Serializable{
        /**
         * 用来在聊天列表展示
         */
        private String title;

        /**
         * 符合markdown格式的文本
         */
        private String text;
    }
}
