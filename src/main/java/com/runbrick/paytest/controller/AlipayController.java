package com.runbrick.paytest.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.runbrick.paytest.util.alipay.AlipayConfig;
import com.runbrick.paytest.util.alipay.BizAlipayService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/alipay")
@AllArgsConstructor
public class AlipayController {
    private static Logger logger = LoggerFactory.getLogger(AlipayController.class);

    AlipayConfig alipayConfig;
    BizAlipayService alipayService;

    /**
     * 支付接口
     *
     * @return
     */
    @GetMapping("/pay")
    public String orderPay() {
        String s = alipayService.appPay("测试支付", String.valueOf(System.currentTimeMillis()), new BigDecimal("0.01").toString());
        logger.info("支付生成信息：{}", s);
        return s;
    }

    /**
     * 订单回调
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/notify")
    public String orderNotify(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        try {
            boolean flag = AlipaySignature.rsaCheckV1(params, alipayConfig.getAlipayPublicKey(), alipayConfig.getCharset(), alipayConfig.getSignType());
            if (flag) {
                logger.info("支付回调信息:{}", params);
                return "success";
            } else {
                return "error";
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝错误回调:{}", e.getErrMsg());
            e.printStackTrace();
            return "error";
        }
    }


    /**
     * 订单退款
     *
     * @return
     * @TODO 仅实现了全部退款
     */
    @RequestMapping(value = "/order_refund", method = RequestMethod.GET)
    public AlipayTradeRefundResponse orderRefund() {
        AlipayTradeRefundResponse refund = alipayService.refund("2022020922001434041429269213", "0.01");
        return refund;
    }
}
