package top.pinyougou.page.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.takefly.core.service.ItemPageService;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Component
public class PageDeleteListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage)message;
        try {
            Long goodsId = Long.parseLong(textMessage.getText());
            System.out.println("要删除的商品详情页的id为:"+goodsId);
            itemPageService.deletePage(goodsId);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
