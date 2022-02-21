package com.runbrick.paytest.util.alipay;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ToString
@Component
@ConfigurationProperties(prefix = "alipay")
public class AlipayConfig extends com.alipay.api.AlipayConfig {

    private String serverUrl;
    private String appId;
    private String privateKey;
    private String format;
    private String charset;
    private String alipayPublicKey;
    private String signType;
    private String notifyUrl;
}
