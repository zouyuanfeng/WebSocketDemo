package com.itzyf.controller;

import com.itzyf.bean.ClientMessage;
import com.itzyf.bean.ServerMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

/**
 * @author 依风听雨
 * @version 创建时间：2018/08/30 17:09
 */
@Controller
public class WebSocketController {

    /**
     * 消息发送工具
     */
    @Autowired
    private SimpMessagingTemplate template;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/index")
    public String index() {
        return "/index";
    }

    @RequestMapping(value = "/single")
    public String single() {
        return "/single";
    }

    /**
     * 接收客户端发送的消息：/app/sendTest
     * 如果订阅了/topic/sendTest，则返回发送的消息
     *
     * @param message
     * @return
     */
    @MessageMapping("/sendTest")
    @SendTo("/topic/sendTest")
    public ServerMessage sendDemo(ClientMessage message) {
        logger.info("接收到了信息" + message.getName());
        return new ServerMessage("你发送的消息为:" + message.getName());
    }

    /**
     * 订阅消息：/app/subscribeTest
     *
     * @return
     */
    @SubscribeMapping("/subscribeTest")
    public ServerMessage sub() {
        logger.info("XXX用户订阅了我。。。");
        return new ServerMessage("感谢你订阅了我。。。");
    }


    /**
     * 调用hello-convert-and-send接口，给订阅的用户发送消息
     *
     * @param name
     */
    @RequestMapping(value = "/hello-convert-and-send", method = RequestMethod.POST)
    @ResponseBody
    public void echoConvertAndSend(@RequestParam("name") String name) {
        template.convertAndSend("/topic/subscribeTest", new ServerMessage("收到消息：" + name));
    }

    /**
     * 同样的发送消息   只不过是ws版本  http请求不能访问
     * 根据用户key发送消息
     *
     * @param message
     * @return
     * @throws Exception
     */
    @MessageMapping("/msg/single")
    public void greeting(Principal principal, ClientMessage message) throws Exception {
        template.convertAndSendToUser(principal.getName(), "/queue/greetings",
                new ServerMessage("single send to：" + message.getId() + ", from:" + message.getName() + "!")
        );
    }

}