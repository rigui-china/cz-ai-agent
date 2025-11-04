package com.chengzhi.czaiagent.agent;

/**
 * @author 徐晟智
 * @version 1.0
 */

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * React 模式的代理类
 * 实现了 思考 - 行动的循环模式
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public abstract class ReActAgent extends BaseAgent{

    /**
     * 代理进行思考，决定是否调用哪些工具类
     * true 代表执行并调用工具，false 表示执行完成，不需要调用
     * @return
     */
    public abstract boolean think();

    /**
     * 具体的每一步执行工具的调用
     * @return
     */
    public abstract String act();

    /**
     * 自己控制这个先思考 再行动的流程
     * @return
     */
    @Override
    public String step() {
        try {
            boolean shouldAct = think();
            if(!shouldAct){
                return "思考完成 - 无需行动";
            }
            return act();
        } catch (Exception e) {
            e.printStackTrace();
            return "步骤执行失败: " + e.getMessage();
        }
    }
}
