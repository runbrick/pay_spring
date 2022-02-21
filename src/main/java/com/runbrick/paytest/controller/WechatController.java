package com.runbrick.paytest.controller;

import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.runbrick.paytest.util.wxpay.BizWechatPayService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wechat/pay")
@AllArgsConstructor
public class WechatController {

    BizWechatPayService wechatPayService;

    private static Logger logger = LoggerFactory.getLogger(WechatController.class);

    /**
     * 创建微信订单给APP
     *
     * @return
     */
    @RequestMapping(value = "/unified/request", method = RequestMethod.GET)
    public Object appPayUnifiedRequest() {
        // totalFee 必须要以分为单位
        Object createOrderResult = wechatPayService.createOrder("测试支付", String.valueOf(System.currentTimeMillis()), 1);
        logger.info("统一下单的生成的参数:{}", createOrderResult);
        return createOrderResult;
    }

    @RequestMapping(method = RequestMethod.POST, value = "notify")
    public String notify(@RequestBody String xmlData) {
        try {
            WxPayOrderNotifyResult result = wechatPayService.wxPayService().parseOrderNotifyResult(xmlData);
            // 支付返回信息
            if ("SUCCESS".equals(result.getReturnCode())) {
                // 可以实现自己的逻辑
                logger.info("来自微信支付的回调:{}", result);
            }

            return WxPayNotifyResponse.success("成功");
        } catch (WxPayException e) {
            logger.error(e.getMessage());
            return WxPayNotifyResponse.fail("失败");
        }
    }

    /**
     * 退款
     *
     * @param transaction_id
     */
    @RequestMapping(method = RequestMethod.POST, value = "refund")
    public void refund(String transaction_id) {
        // totalFee 必须要以分为单位,退款的价格可以这里只做的全部退款
        WxPayRefundResult refund = wechatPayService.refund(transaction_id, 1);
        // 实现自己的逻辑
        logger.info("退款本地回调:{}", refund);
    }

    /**
     * 退款回调
     *
     * @param xmlData
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "refund_notify")
    public String refundNotify(@RequestBody String xmlData) {
        // 实现自己的逻辑
        logger.info("退款远程回调:{}", xmlData);
        // 必须要返回 SUCCESS 不过有 WxPayNotifyResponse 给整合成了 xml了
        return WxPayNotifyResponse.success("成功");
    }

}
