package com.runbrick.paytest.util.wxpay;

import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.net.InetAddress;

/**
 * 微信支付
 */
@Service
@ConditionalOnClass(WxPayService.class)
@EnableConfigurationProperties(WechatPayConfig.class)
@AllArgsConstructor
public class BizWechatPayService {

    private WechatPayConfig wechatPayConfig;


    public WxPayService wxPayService() {
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(wechatPayConfig.getAppId());
        payConfig.setMchId(wechatPayConfig.getMchId());
        payConfig.setMchKey(wechatPayConfig.getMchKey());
        payConfig.setKeyPath(wechatPayConfig.getKeyPath());
        payConfig.setPrivateKeyPath(wechatPayConfig.getPrivateKeyPath());
        payConfig.setPrivateCertPath(wechatPayConfig.getPrivateCertPath());
        // 可以指定是否使用沙箱环境
        payConfig.setUseSandboxEnv(false);
        payConfig.setSignType("MD5");

        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(payConfig);
        return wxPayService;
    }


    /**
     * 创建微信订单给APP
     *
     * @param productTitle 商品标题
     * @param outTradeNo   订单号
     * @param totalFee     总价
     * @return
     */
    public Object createOrder(String productTitle, String outTradeNo, Integer totalFee) {
        try {
            WxPayUnifiedOrderRequest request = new WxPayUnifiedOrderRequest();
            // 支付描述
            request.setBody(productTitle);
            // 订单号
            request.setOutTradeNo(outTradeNo);
            // 请按照分填写
            request.setTotalFee(totalFee);
            // 回调链接
            request.setNotifyUrl(wechatPayConfig.getNotifyUrl());
            // 终端IP.
            request.setSpbillCreateIp(InetAddress.getLocalHost().getHostAddress());
            // 设置类型为APP
            request.setTradeType(WxPayConstants.TradeType.APP);
            // 一定要用 createOrder 不然得自己做二次校验
            Object order = wxPayService().createOrder(request);
            return order;
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 退款
     *
     * @param tradeNo
     * @param totalFee
     * @return
     */
    public WxPayRefundResult refund(String tradeNo, Integer totalFee) {
        WxPayRefundRequest wxPayRefundRequest = new WxPayRefundRequest();
        wxPayRefundRequest.setTransactionId(tradeNo);
        wxPayRefundRequest.setOutRefundNo(String.valueOf(System.currentTimeMillis()));
        wxPayRefundRequest.setTotalFee(totalFee);
        wxPayRefundRequest.setRefundFee(totalFee);
        wxPayRefundRequest.setNotifyUrl(wechatPayConfig.getRefundNotifyUrl());
        try {
            WxPayRefundResult refund = wxPayService().refundV2(wxPayRefundRequest);
            if (refund.getReturnCode().equals("SUCCESS") && refund.getResultCode().equals("SUCCESS")) {
                return refund;
            }

        } catch (WxPayException e) {
            e.printStackTrace();
        }
        return null;
    }
}
