package com.kebikeren.consoletable.utils;

/**
 * @Classname Validation
 * @Description 验证
 * @Date 2021-10-08
 * @Created by kebikeren
 */
public class Validation {
    //返回大于等于传入参数的最小的偶数
    public static int evenNumber(int number) {
        return number % 2 == 0 ? number : number + 1;
    }
}
