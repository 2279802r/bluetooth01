package com.bluetooth01.bluetooth01;

import android.os.Handler;

/**
 * Created by ranjiaqing on 17/7/19.
 */

public class Task {
    /**
     * 请求等待蓝牙连接（作为服务器）
     */
    public static final int TASK_START_ACCEPT = 1;
    /**
     * 请求连接远程蓝牙设备（作为客户端）
     */
    public static final int TASK_START_CONN_THREAD = 2;
    /**
     * 发送消息
     */
    public static final int TASK_SEND_MSG = 3;
    /**
     * 获得蓝牙运行状态
     */
    public static final int TASK_GET_REMOTE_STATE = 4;
    /**
     * 接受到蓝牙聊天消息
     */
    public static final int TASK_RECV_MSG = 5;


    // 任务ID
    private int mTaskID;
    // 任务参数列表
    public Object[] mParams;

    private Handler mH;

    public Task(Handler handler, int taskID, Object[] params){
        this.mH = handler;
        this.mTaskID = taskID;
        this.mParams = params;
    }

    public Handler getHandler(){
        return this.mH;
    }

    public int getTaskID(){
        return mTaskID;
    }
}
