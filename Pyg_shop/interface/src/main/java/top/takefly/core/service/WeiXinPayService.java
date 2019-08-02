package top.takefly.core.service;

import java.util.Map;

public interface WeiXinPayService {

    /**
     * 生成支付二维码
     * @param out_trade_no
     * @param total_Fee
     * @return
     */
    public Map createNative(String out_trade_no , String total_Fee);


    /**
     * 查询支付状态
     * @param out_trade_no
     * @return
     */
    public Map queryPayStatus(String out_trade_no);

}
