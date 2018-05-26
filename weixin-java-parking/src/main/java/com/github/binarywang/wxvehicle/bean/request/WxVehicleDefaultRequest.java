package com.github.binarywang.wxvehicle.bean.request;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * <pre>
 *  支付请求默认对象类
 *  Created by BinaryWang on 2017/6/18.
 * </pre>
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@XStreamAlias("xml")
public class WxVehicleDefaultRequest extends BaseWxVehicleRequest {
  @Override
  protected void checkConstraints() {
    //do nothing
  }
}
