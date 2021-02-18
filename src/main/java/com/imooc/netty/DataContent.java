package com.imooc.netty;

import java.io.Serializable;

public class DataContent implements Serializable {

    private static final long serialVersionUID = 8021381444738260454L;

    private Integer action;
    private ChatMsg chatMsg;
    private String extand; //扩展字段

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public ChatMsg getChatMsg() {
        return chatMsg;
    }

    public void setChatMsg(ChatMsg chatMsg) {
        this.chatMsg = chatMsg;
    }

    public String getExtand() {
        return extand;
    }

    public void setExtand(String extand) {
        this.extand = extand;
    }
}
