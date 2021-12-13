package com.lh.grader.message;


import com.lh.grader.util.TimeUtil;

/**
 * 收到的日志
 */

public class RecvMessage implements IMessage {

    private String command;
    private String message;
    private String mName;

    public RecvMessage(String command, String name) {
        this.command = command;
        this.message = command;
        this.mName = name;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public boolean isToSend() {
        return false;
    }

}
