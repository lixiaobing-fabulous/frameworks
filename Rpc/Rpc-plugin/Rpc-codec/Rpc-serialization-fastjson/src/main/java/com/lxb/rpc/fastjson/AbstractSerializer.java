package com.lxb.rpc.fastjson;


import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.lxb.rpc.exception.SerializerException;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * 序列化基础类
 */
public class AbstractSerializer {

    protected static final int NONE = 0;
    protected static final int BEFORE = NONE + 1;
    protected static final int AFTER = NONE + 2;

    /**
     * 写值
     *
     * @param serializer 输出
     * @param field      字段
     * @param value      值
     */
    protected void writeString(final JSONSerializer serializer, final String field, final String value) {
        writeString(serializer.out, field, value, true, AFTER);
    }

    /**
     * 写值
     *
     * @param out   输出
     * @param field 字段
     * @param value 值
     */
    protected void writeString(final SerializeWriter out, final String field, final String value) {
        writeString(out, field, value, true, AFTER);
    }

    /**
     * 写值
     *
     * @param out        输出
     * @param field      字段
     * @param value      值
     * @param ignoreNull 是否忽略null值
     * @param separator  分隔符位置
     */
    protected void writeString(final SerializeWriter out, final String field, final String value,
                               final boolean ignoreNull, final int separator) {
        if (value != null || !ignoreNull) {
            if (separator == BEFORE) {
                out.write(',');
            }
            out.writeFieldName(field);
            if (value == null) {
                out.writeNull();
            } else {
                out.writeString(value);
            }
            if (separator == AFTER) {
                out.write(',');
            }
        }
    }

    /**
     * 写值
     *
     * @param serializer 输出
     * @param field      字段
     * @param value      值
     * @param separator  分隔符位置
     */
    protected void write(final JSONSerializer serializer, final String field, final Object value, final int separator) {
        write(serializer, field, value, false, separator);
    }

    /**
     * 开始对象输出
     *
     * @param serializer 输出
     */
    protected void writeObjectBegin(final JSONSerializer serializer) {
        serializer.out.write('{');
    }

    /**
     * j结束对象输出
     *
     * @param serializer 输出
     */
    protected void writeObjectEnd(final JSONSerializer serializer) {
        serializer.out.write('}');
    }

    /**
     * 写值
     *
     * @param serializer 输出
     * @param field      字段
     * @param value      值
     * @param ignoreNull 是否忽略null值
     * @param separator  分隔符位置
     */
    protected void write(final JSONSerializer serializer, final String field, final Object value,
                         final boolean ignoreNull, final int separator) {
        if (value != null || !ignoreNull) {
            SerializeWriter out = serializer.getWriter();
            if (separator == BEFORE) {
                out.write(',');
            }
            out.writeFieldName(field);
            serializer.write(value);
            if (separator == AFTER) {
                out.write(',');
            }
        }
    }

    /**
     * 读取字符串
     *
     * @param lexer    文法
     * @param field    字段
     * @param nullable 是否可以null
     */
    protected String parseString(final JSONLexer lexer, final String field, final boolean nullable) {
        String result = null;
        switch (lexer.token()) {
            case JSONToken.LITERAL_STRING:
                result = lexer.stringVal();
                lexer.nextToken();
                break;
            case JSONToken.NULL:
                if (!nullable) {
                    throw new SerializerException("syntax error: invalid " + field);
                }
                lexer.nextToken();
                break;
            default:
                throw new SerializerException("syntax error: invalid " + field);
        }
        return result;
    }

    /**
     * 读取MAP
     *
     * @param parser 解析器
     * @param lexer  文法
     * @param field  字段
     */
    protected Map<String, Object> parseMap(final DefaultJSONParser parser, final JSONLexer lexer, final String field) {
        Map<String, Object> result = null;
        switch (lexer.token()) {
            case JSONToken.LBRACE:
                result = parser.parseObject();
                break;
            case JSONToken.NULL:
                lexer.nextToken();
                break;
            default:
                throw new SerializerException("syntax error: invalid " + field);
        }
        return result;
    }

    /**
     * 读取字符串数组
     *
     * @param parser 解析器
     * @param lexer  文法
     * @param field  字段
     */
    protected String[] parseStrings(final DefaultJSONParser parser, final JSONLexer lexer, final String field) {
        String result[] = null;
        switch (lexer.token()) {
            case JSONToken.LBRACKET:
                result = parser.parseObject(String[].class);
                break;
            case JSONToken.NULL:
                lexer.nextToken();
                break;
            default:
                throw new SerializerException("syntax error: invalid " + field);
        }
        return result;
    }

    /**
     * 解析对象
     *
     * @param parser 解析器
     * @param lexer  语法
     * @param type   类型
     * @return
     */
    protected Object parseObject(final DefaultJSONParser parser, final JSONLexer lexer, final Type type) {
        return lexer.token() != JSONToken.NULL ? parser.parseObject(type) : null;
    }

    /**
     * 解析对象数组
     *
     * @param parser 解析器
     * @param lexer  语法
     * @param types  类型
     * @param field  字段
     */
    protected Object[] parseObjects(final DefaultJSONParser parser, final JSONLexer lexer, final Type[] types, final String field) {
        Object[] result = null;
        //空数组
        if (lexer.token() == JSONToken.NULL) {
            if (types.length == 0) {
                lexer.nextToken();
            } else {
                throw new SerializerException("syntax error: invalid " + field);
            }
        } else {
            //解析参数
            JSONReader reader = new JSONReader(parser);
            reader.startArray();
            int i = 0;
            result = new Object[types.length];
            while (reader.hasNext()) {
                if (i >= result.length) {
                    throw new SerializerException("syntax error: invalid " + field);
                }
                result[i] = reader.readObject(types[i]);
                i++;
            }
            reader.endArray();
        }
        return result;
    }
}
