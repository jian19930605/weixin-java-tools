package com.github.binarywang.wxpay.service.impl;

import com.github.binarywang.wxpay.bean.papay.EntPayBankRequest;
import com.github.binarywang.wxpay.bean.papay.EntPayBankResult;
import com.github.binarywang.wxpay.bean.papay.EntPayRequest;
import com.github.binarywang.wxpay.constant.WxPayConstants.CheckNameOption;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.testbase.ApiTestModule;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

/**
 * <pre>
 *  企业付款测试类.
 *  Created by BinaryWang on 2017/12/19.
 * </pre>
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Test
@Guice(modules = ApiTestModule.class)
public class EntPayServiceImplTest {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Inject
  private WxPayService payService;

  @Test
  public void testEntPay() throws WxPayException {
    EntPayRequest request = EntPayRequest.newBuilder()
      .partnerTradeNo("1024196955984625665")
      .amount(30)
      .spbillCreateIp("172.16.7.91")
      .checkName(CheckNameOption.NO_CHECK)
      .description("星富通")
      .openid("otbjl5TS9WqLRx0n3uFQWaz2y-Q4")
      .build();

    this.logger.info(this.payService.getEntPayService().entPay(request).toString());
  }

  @Test
  public void testQueryEntPay() throws WxPayException {
    this.logger.info(this.payService.getEntPayService().queryEntPay("11212121").toString());
  }

  @Test
  public void testGetPublicKey() throws Exception {
    this.logger.info(this.payService.getEntPayService().getPublicKey());
  }

  @Test
  public void testPayBank() throws Exception {
    EntPayBankResult result = this.payService.getEntPayService().payBank(EntPayBankRequest.builder()
      .bankCode("aa")
      .amount(1)
      .encBankNo("1")
      .encTrueName("2")
      .partnerTradeNo("3")
      .description("11")
      .build());
    this.logger.info(result.toString());
  }

  @Test
  public void testQueryPayBank() throws Exception {
    this.logger.info(this.payService.getEntPayService().queryPayBank("123").toString());
  }
}
