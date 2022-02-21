package com.runbrick.paytest.util.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 阿里云支付类
 */
@Service
public class BizAlipayService {

    private static Logger logger = LoggerFactory.getLogger(BizAlipayService.class);

    @Autowired
    AlipayConfig alipayConfig;

    private DefaultAlipayClient client() throws AlipayApiException {
        return new DefaultAlipayClient(alipayConfig);
    }

    /**
     * 预下单
     *
     * @param subject     订单标题
     * @param outTradeNo  商家生成的订单号
     * @param totalAmount 订单总价值
     * @return
     */
    public String appPay(String subject, String outTradeNo, String totalAmount) {
        String source = "";
        try {
            DefaultAlipayClient client = client();
            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
            model.setSubject(subject);
            model.setOutTradeNo(outTradeNo);
            model.setTotalAmount(totalAmount);
            // alipay 封装的接口调用
            AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
            request.setBizModel(model);
            request.setNotifyUrl(alipayConfig.getNotifyUrl());
            AlipayTradeAppPayResponse response = client.sdkExecute(request);
            source = response.getBody();
        } catch (AlipayApiException e) {
            logger.error("支付出现问题,详情：{}", e.getErrMsg());
            e.printStackTrace();
        }
        return source;
    }

    /**
     * 退款
     *
     * @param tradeNo
     * @param totalAmount
     * @return
     */
    public AlipayTradeRefundResponse refund(String tradeNo, String totalAmount) {
        try {
            DefaultAlipayClient client = client();
            AlipayTradeRefundModel alipayTradeRefundModel = new AlipayTradeRefundModel();
            alipayTradeRefundModel.setTradeNo(tradeNo);
            alipayTradeRefundModel.setRefundAmount(totalAmount);

            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            request.setBizModel(alipayTradeRefundModel);
            AlipayTradeRefundResponse response = client.execute(request);
            return response;
        } catch (AlipayApiException e) {
            logger.error("退款出现问题,详情：{}", e.getErrMsg());
            e.printStackTrace();
        }
        return null;
    }


}
