package com.lh.grader.message;


import com.lh.grader.util.TimeUtil;

/**
 * 发送的日志
 */

public class SendMessage implements IMessage {

    private String command;
    private String message;
    private String mName;

    public SendMessage(String command,String name) {
        this.command = command;
        this.message = TimeUtil.currentTime() + "    发送命令：" + command;
        this.mName=name;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public boolean isToSend() {
        return true;
    }

    @Override
    public String getName() {
        return mName;
    }
}
