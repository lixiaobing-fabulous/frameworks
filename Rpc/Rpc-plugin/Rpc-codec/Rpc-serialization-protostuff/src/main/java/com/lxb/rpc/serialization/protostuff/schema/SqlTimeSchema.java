package com.lxb.rpc.serialization.protostuff.schema;


import java.sql.Time;

/**
 * SQL Time
 */
public class SqlTimeSchema extends AbstractSqlDateSchema<Time> {

    public static final SqlTimeSchema INSTANCE = new SqlTimeSchema();

    public SqlTimeSchema() {
        super(Time.class);
    }

    @Override
    public Time newMessage() {
        return new Time(0);
    }

}
