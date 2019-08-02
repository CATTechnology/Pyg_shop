package top.takefly.buyer.service.impl;

import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Value;
import pojo.utils.HttpClient;
import top.takefly.core.service.WeiXinPayService;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: Pyg_shop
 * @description: 实现WeiXinPayService接口
 * @author: 戴灵飞
 * @create: 2019-07-11 11:42
 **/
public class WeiXinPayServiceImpl implements WeiXinPayService {

    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner;

    @Value("${partnerkey}")
    private String partnerkey;

    @Value("${notifyurl}")
    private String notifyurl;

    @Override
    public Map createNative(String out_trade_no, String total_Fee) {
        Map map = new HashMap();
        //公众号
        map.put("appid" , appid );
        //商户号
        map.put("mch_id" , partner);
        //生成随机字符串
        map.put("nonce_str" , WXPayUtil.generateNonceStr());
        //总金额
        map.put("total_fee" , total_Fee);
        //ip
        map.put("spbill_create_ip" , "127.0.0.1");
        //回调地址(随便写)
        map.put("notify_url", "http://www.takefly.top");
        //交易类型
        map.put("trade_type", "NATIVE");
        try{
            //生成要发送的xml
            String xmlParam = WXPayUtil.generateSignedXml(map, partnerkey);
            System.out.println(xmlParam);
            HttpClient client =new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            //获取返回的结果
            String result = client.getContent();
            System.out.println(result);
            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
            Map<String, String> graphMap = WXPayUtil.xmlToMap(result);
            graphMap.put("code_url", resultMap.get("code_url"));
            //总金额
            graphMap.put("total_fee", total_Fee);
            //订单号
            graphMap.put("out_trade_no",out_trade_no);

            return graphMap;
        }catch(Exception e){
            e.printStackTrace();
            return new HashMap(16);
        }
    }

    /**
     * 查询支付状态
     * @param out_trade_no
     * @return
     */
    @Override
    public Map queryPayStatus(String out_trade_no) {
        Map map = new HashMap();
        //公众号
        map.put("appid" , appid);
        //商户号
        map.put("mch_id" , appid);
        //随机字符串
        map.put("nonce_str" , WXPayUtil.generateNonceStr());
        //商家订单号
        map.put("out_trade_no",  out_trade_no);
        String url = "https://api.mch.weixin.qq.com/pay/orderquery";
        try{
            String xmlParam = WXPayUtil.generateSignedXml(map, partnerkey);
            HttpClient httpClient  = new HttpClient(url);
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam );
            httpClient.post();
            
            //获取返回结果
            String content = httpClient.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
            System.out.println(resultMap);
            return resultMap;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
