package com.mengka.cloud.flexypoolsample;

import java.io.Serializable;

/**
 * @author huangyy
 * @date 2018/02/21.
 */
public class MessageExt implements Serializable {

    // 消息ID
    private String msgId;
    //消息体
    private byte[] body;

    private String tags;

    private String keys;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }
}
