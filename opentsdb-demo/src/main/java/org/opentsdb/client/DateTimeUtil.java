package org.opentsdb.client;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {
    public static Date parse(String date, String fm) {
        Date res = null;
        try {
            SimpleDateFormat sft = new SimpleDateFormat(fm);
            res = sft.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

    private static final SimpleDateFormat sdfLong = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
    private static final SimpleDateFormat sdfShort = new SimpleDateFormat("yyyy/MM/dd");

    /**
     * 日期时间格式字符串转换为Date类型
     *
     * @param string
     * @return
     * @throws Exception
     */
    public static Date stringLongToDate(String string) throws Exception {
        return sdfLong.parse(string);
    }

    /**
     * 日期格式字符串转换为Date类型
     * @param string
     * @return
     * @throws Exception
     */
    public static Date stringShortToDate(String string) throws Exception {
        return sdfShort.parse(string);
    }

    /**
     * 长整型毫秒(自1970-01-01  00:00:00 GMT过去的毫秒数，又称Unix时间戳)转换为Date类型
     * @param millisecond
     * @return
     */
    public static Date millisecondToDate(long millisecond) {
        return new Date(millisecond);
    }

    /**
     * 日期时间格式字符串转换为(Unix时间戳)长整型类型
     * @param string
     * @return
     * @throws Exception
     */
    public static long stringLongToMillisecond(String string) throws Exception {
        return stringLongToDate(string).getTime();
    }

    /**
     * 日期格式字符串转换为(Unix时间戳)长整型类型
     * @param string
     * @return
     * @throws Exception
     */
    public static long stringShortToMillisecond(String string) throws Exception {
        return stringShortToDate(string).getTime();
    }

    /**
     * Date类型转换为(Unix时间戳)长整型类型
     * @param date
     * @return
     */
    public static long dateToMillisecond(Date date) {
        return date.getTime();
    }

    /**
     * (Unix时间戳)长整型类型转换为日期时间格式字符串
     * @param millisecond
     * @return
     */
    public static String millisecondToStringLong(long millisecond) {
        return sdfLong.format(millisecond);
    }

    /**
     * Date类型转换为日期时间格式字符串
     * @param date
     * @return
     */
    public static String dateToStringLong(Date date) {
        return sdfLong.format(date);
    }

    /**
     * (Unix时间戳)长整型类型转换为日期格式字符串
     * @param millisecond
     * @return
     */
    public static String millisecondToStringShort(long millisecond) {
        return sdfShort.format(millisecond);
    }

    /**
     * Date类型转换为日期格式字符串
     * @param date
     * @return
     */
    public static String dateToStringShort(Date date) {
        return sdfShort.format(date);
    }

}

