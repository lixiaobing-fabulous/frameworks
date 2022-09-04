package com.lxb.extension;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

/**
 * 日期函数
 */
@FunctionalInterface
public interface DateParser {

    /**
     * 把字符串转换成日期
     *
     * @param value 文本
     * @return 时间对象
     * @throws ParseException
     */
    Date parse(String value) throws ParseException;


    /**
     * 基于日期格式化进行转换
     */
    class SimpleDateParser implements DateParser {
        //日期格式化
        protected SimpleDateFormat format;

        public SimpleDateParser(SimpleDateFormat format) {
            this.format = format;
        }

        @Override
        public Date parse(final String value) throws ParseException {
            return format == null || value == null || value.isEmpty() ? null : format.parse(value);
        }
    }

    /**
     * 采用DateTimeFormatter进行转换
     */
    class DateTimeParser implements DateParser {
        /**
         * 时间格式化
         */
        protected DateTimeFormatter formatter;

        public DateTimeParser(final DateTimeFormatter formatter) {
            this.formatter = formatter;
        }

        @Override
        public Date parse(final String value) throws ParseException {
            try {
                return formatter == null || value == null || value.isEmpty() ? null : Date.from(Instant.from(LocalDateTime.parse(value, formatter)));
            } catch (DateTimeParseException e) {
                throw new ParseException(e.getMessage(), e.getErrorIndex());
            }
        }
    }
}
