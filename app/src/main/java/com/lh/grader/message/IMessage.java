package com.lh.grader.message;

/**
 * 日志消息数据接口
 */

public interface IMessage {
    /**
     * 消息文本
     *
     * @return
     */
    String getMessage();

    /**
     * 是否发送的消息
     *
     * @return
     */
    boolean isToSend();


    String getName();
}
