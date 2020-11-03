package xyz.seiyaya.jenkins.plugin.help;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import xyz.seiyaya.jenkins.plugin.bean.DingTalkMessageText;
import xyz.seiyaya.jenkins.plugin.helper.HttpHelper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 钉钉消息发送相关
 * @author wangjia
 * @version 1.0
 * @date 2020/11/3 14:24
 */
@Slf4j
public class DingTalkMessageHelper {


    private static final String PREFIX_URL = "https://oapi.dingtalk.com/robot/send?access_token=%s&sign=%s&timestamp=%s";

    /**
     * 发送文本消息
     * @param token
     * @param secret
     * @param dingTalkMessageText
     */
    public static void sendTextMessage(String token, String secret, DingTalkMessageText dingTalkMessageText){
        try {
            long current = System.currentTimeMillis();
            String sign = getSign(System.currentTimeMillis(),secret);
            Gson gson = new Gson();
            gson.toJson(dingTalkMessageText);
            String s = HttpHelper.sendPostJson(String.format(PREFIX_URL, token, sign,current), gson.toJson(dingTalkMessageText));
            log.info("消息发送成功,响应结果集:{}",s);
        }catch (Exception e){
            log.error("DingTalk消息发送失败:",e);
        }
    }

    public static String getSign(Long timestamp, String secret) throws Exception {
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), String.valueOf(StandardCharsets.UTF_8));
        return sign;
    }
}
