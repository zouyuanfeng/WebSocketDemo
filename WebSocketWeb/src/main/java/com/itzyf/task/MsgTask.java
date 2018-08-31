package com.itzyf.task;

import com.itzyf.bean.ServerMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author 依风听雨
 * @version 创建时间：2018/08/28 09:50
 */
@Component
public class MsgTask {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 定义每过3秒执行任务
     */
    @Scheduled(fixedRate = 3000)
    public void reportCurrentTime() {
        String date = sdf.format(new Date());
        System.out.println("现在时间：" + date);
        messagingTemplate.convertAndSend("/topic/subscribeTest", new ServerMessage("现在时间：" + date));
    }
}
