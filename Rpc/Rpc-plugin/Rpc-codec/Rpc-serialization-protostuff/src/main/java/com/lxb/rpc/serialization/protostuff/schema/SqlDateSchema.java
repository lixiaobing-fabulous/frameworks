package com.lxb.rpc.serialization.protostuff.schema;


import java.sql.Date;

/**
 * SQL Date
 */
public class SqlDateSchema extends AbstractSqlDateSchema<Date> {

    public static final SqlDateSchema INSTANCE = new SqlDateSchema();

    public SqlDateSchema() {
        super(Date.class);
    }

    @Override
    public Date newMessage() {
        return new Date(0);
    }

}
