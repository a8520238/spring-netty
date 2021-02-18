package com.imooc.netty;

import com.imooc.SpringUtil;
import com.imooc.enums.MsgActionEnum;
import com.imooc.service.UserService;
import com.imooc.utils.JsonUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//TextWebSocketFrame 在netty中，用于为websocket专门处理文本的对象，frame是消息的载体
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    //用于记录和管理所有客户端的channel
    public static ChannelGroup users =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        String content = textWebSocketFrame.text();
        Channel currentChannel = channelHandlerContext.channel();

        //1.获取客户端发来的消息
        DataContent dataContent = JsonUtils.jsonToPojo(content, DataContent.class);
        Integer action = dataContent.getAction();
        //2.判断消息类型，根据不同类型处理业务
        if (action == MsgActionEnum.CONNECT.type) {
            //2.1 当websocket第一次open的时候，初始化channel,把用的channel和userId关联
            String senderId = dataContent.getChatMsg().getSenderId();
            UserChannelRel.put(senderId, currentChannel);

            //测试
            for (Channel c: users) {
                System.out.println(c.id().asLongText());
            }
            UserChannelRel.output();
        } else if (action == MsgActionEnum.CHAT.type) {
            //2.2 聊天类型的消息，把聊天记录保存数据库，同时标记消息的签收状态
            ChatMsg chatMsg = dataContent.getChatMsg();
            String msgText = chatMsg.getMsg();
            String receiverId = chatMsg.getReceiverId();
            String senderId = chatMsg.getSenderId();

            UserService userService = (UserService) SpringUtil.getBean("userServiceImpl");

            String msgId = userService.saveMsg(chatMsg);
            chatMsg.setMsgId(msgId);

            DataContent dataContentMsg = new DataContent();
            dataContent.setChatMsg(chatMsg);
            //发送消息
            Channel receiverChannel = UserChannelRel.get(receiverId);
            if (receiverChannel == null) {
                //Todo channel 为空代表用户离线，推送消息
            } else {
                //当receiverChannel不为空，从ChannelGroup去查找对应的channel是否存在
                Channel findChannel = users.find(receiverChannel.id());
                if (findChannel != null) {
                    //用户在线
                    receiverChannel.writeAndFlush(
                            new TextWebSocketFrame(
                                    JsonUtils.objectToJson(dataContent)));
                } else {
                    //todo 用户离线 推送
                }
            }
        } else if (action == MsgActionEnum.SIGNED.type) {
            //2.3 签收消息，针对具体消息进行签收，修改数据库中对应的消息的签收状态
            UserService userService = (UserService) SpringUtil.getBean("userServiceImpl");
            //扩展字段在signed类型的消息中，代表需要去签收的消息id,逗号间隔
            String msgIdsStr = dataContent.getExtand();
            String msgIds[] = msgIdsStr.split(",");
            //System.out.println("ok");
            List<String> msgIdList = new ArrayList<>();
            for (String mid: msgIds) {
                if (StringUtils.isNoneBlank(mid)) {
                    msgIdList.add(mid);
                }
            }
            System.out.println(msgIdList.toString());
            if (msgIdList != null && !msgIdList.isEmpty() && msgIdList.size() > 0) {
                userService.updateMsgSigned(msgIdList);
            }
        } else if (action == MsgActionEnum.KEEPALIVE.type) {
            //2.4 心跳类型的消息
            System.out.println("收到来自channel为" + currentChannel + "的心跳包");
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        users.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //当触发handlerRemoved, ChannelGroup会自动移除对应的channel
        users.remove(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        //发生异常关闭连接， 随后从channelgroup移除
        ctx.channel().close();
        users.remove(ctx.channel());
    }
}
