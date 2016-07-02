package com.jean.music.utils;

import java.util.UUID;

public class RandomUtil {

    /**
     * 生成uuid
     *
     * @return
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 获取指定范围的随机数
     *
     * @param start
     * @param end
     * @return [start-end]范围的随机数
     */
    public static long getRandom(long start, long end) {
        return Math.round(Math.random() * (end - start) + start);
    }
}
