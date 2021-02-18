package com.imooc.netty;

import java.io.Serializable;

public class ChatMsg implements Serializable {


    private static final long serialVersionUID = 3611169682695799175L;

    private String senderId; //发送者id
    private String receiverId; //接受者id
    private String msg;
    private String msgId;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
