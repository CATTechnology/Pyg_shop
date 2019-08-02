package top.takefly.pinyougou.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.takefly.core.service.ItemSearchService;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.Arrays;

@Component
public class ItemDeleteListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage)message;
        try {
            Long [] itemsId = (Long[]) objectMessage.getObject();
            System.out.println("批量删除索引的商品的id为"+ Arrays.asList(itemsId));
            //批量删除
            itemSearchService.BatchDeleteById(itemsId);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
