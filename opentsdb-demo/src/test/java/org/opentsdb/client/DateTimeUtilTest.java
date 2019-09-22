package org.opentsdb.client;

import java.util.Date;

public class DateTimeUtilTest {
    public static void main(String[] args) {
        // 获取当前时间的Unix时间戳
        long millisecond = System.currentTimeMillis();
        // Date实例化时，如果不传入参数，则是当前时间
        Date date = new Date();
        String stringLong = "2019/09/05 10:27:29:879";
        String stringShort = "2018/09/05";
        System.out.println("Date转Unix时间戳：" + DateTimeUtil.dateToMillisecond(date));
        System.out.println("Date转日期时间格式字符串：" + DateTimeUtil.dateToStringLong(date));
        System.out.println("Date转日期格式字符串：" + DateTimeUtil.dateToStringShort(date));
        System.out.println("Unix时间戳转Date：" + DateTimeUtil.millisecondToDate(millisecond));
        System.out.println("Unix时间戳转日期时间格式字符串：" + DateTimeUtil.millisecondToStringLong(millisecond));
        System.out.println("Unix时间戳转日期格式字符串：" + DateTimeUtil.millisecondToStringShort(millisecond));
        try {
            System.out.println("日期时间格式字符串转Date:" + DateTimeUtil.stringLongToDate(stringLong));
            System.out.println("日期时间格式字符串转Unix时间戳:" + DateTimeUtil.stringLongToMillisecond(stringLong));
            System.out.println("日期格式字符串转Date:" + DateTimeUtil.stringShortToDate(stringShort));
            System.out.println("日期格式字符串转Unix时间戳:" + DateTimeUtil.stringShortToMillisecond(stringShort));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
