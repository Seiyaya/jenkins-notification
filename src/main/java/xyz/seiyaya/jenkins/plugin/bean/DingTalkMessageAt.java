package xyz.seiyaya.jenkins.plugin.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wangjia
 * @version 1.0
 * @date 2020/11/3 14:13
 */
@Data
public class DingTalkMessageAt implements Serializable {

    private List<String> atMobiles;

    private Boolean isAtAll;
}
