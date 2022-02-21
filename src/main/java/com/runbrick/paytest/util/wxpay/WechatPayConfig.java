package com.runbrick.paytest.util.wxpay;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "wxpay")
public class WechatPayConfig {
    private String appId;
    private String mchId;
    private String mchKey;
    private String keyPath;
    private String privateKeyPath;
    private String privateCertPath;
    private String notifyUrl;
    private String refundNotifyUrl;
}
