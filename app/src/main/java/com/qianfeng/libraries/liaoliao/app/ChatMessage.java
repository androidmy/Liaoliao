package com.qianfeng.libraries.liaoliao.app;

/**
 * Created by aaa on 15-4-24.
 */

/*
* 描述聊天的消息
* */
public class ChatMessage {

    public static final int SOURCE_TYPE_RECEIVED=1;//收到的
    public static final int  SOURCE_TYPE_SEND=0; //发出去的
    private    String  from;//发送信息的人
    private  String    to;//接收信息的人
    private   String    body;//信息的内容
    private   long     time;//信息发送的时间
    private    int    sourceType;//消息的来源类型     代表是发出的还是收到的
                                   //可选值  0是发出去    1收到的


    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }
}
