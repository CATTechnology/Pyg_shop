package top.takefly.pinyougou.listener;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.takefly.core.pojo.item.Item;
import top.takefly.core.service.ItemSearchService;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

@Component
public class ItemAddListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        TextMessage text = (TextMessage)message;
        try {
            System.out.println("ItemSearchService接收到的数据是:"+text.getText());
            List<Item> items = JSON.parseArray(text.getText(), Item.class);
            //调用索引添加
            itemSearchService.addItemToSolr(items);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
