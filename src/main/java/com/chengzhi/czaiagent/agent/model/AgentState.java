package com.chengzhi.czaiagent.agent.model;

/**
 * @author 徐晟智
 * @version 1.0
 */

/**
 * 代理执行状态的枚举类
 */
public enum AgentState {

    /**
     * 空闲
     */
    IDLE,

    /**
     * 运行状态
     */
    RUNNING,

    /**
     * 执行结束
     */
    FINISHED,

    /**
     * 发生错误
     */
    ERROR
}
