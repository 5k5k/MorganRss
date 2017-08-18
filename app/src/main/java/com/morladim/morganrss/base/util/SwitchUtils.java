package com.morladim.morganrss.base.util;

/**
 * <br>创建时间：2017/8/16.
 *
 * @author morladim
 */
public class SwitchUtils {

    /**
     * 获取开关状态，1起始
     *
     * @param value 存储开关状态的整形值
     * @param index 要获取的开关在整形值中是第几位
     * @return 开关状态
     */
    public static boolean getState(int value, int index) {
        return (value >> (index - 1) & 1) == 1;
    }

    /**
     * 设置开关状态，1起始
     *
     * @param value 存储开关状态的整形值
     * @param index 要设置的开关在整形值中是第几位
     * @param state 开关状态
     * @return 存储开关的整形值值
     */
    public static int setState(int value, int index, boolean state) {
        index--;
        if (state) {
            value = 1 << index | value;
        } else {
            value = ~(1 << index) & value;
        }
        return value;
    }
}
