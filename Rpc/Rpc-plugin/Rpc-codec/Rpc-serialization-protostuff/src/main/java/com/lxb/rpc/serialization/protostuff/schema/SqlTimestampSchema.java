package com.lxb.rpc.serialization.protostuff.schema;


import java.sql.Timestamp;

/**
 * SQL Timestamp
 */
public class SqlTimestampSchema extends AbstractSqlDateSchema<Timestamp> {

    public static final SqlTimestampSchema INSTANCE = new SqlTimestampSchema();

    public SqlTimestampSchema() {
        super(Timestamp.class);
    }

    @Override
    public Timestamp newMessage() {
        return new Timestamp(0);
    }

}
