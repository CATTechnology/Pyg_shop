package top.pinyougou.page.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.takefly.core.service.ItemPageService;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Component
public class PageGenerateListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage)message;
        try {
            Long id = Long.parseLong(textMessage.getText());
            System.out.println("要生成页面的商品id为:"+id);
            itemPageService.genItemHtml(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
