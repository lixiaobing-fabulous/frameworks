package com.lxb.rpc.serealization.model;


import com.lxb.rpc.codec.serialization.Codec;
import com.lxb.rpc.codec.serialization.ObjectReader;
import com.lxb.rpc.codec.serialization.ObjectWriter;

import java.io.IOException;
import java.util.Arrays;

public class Apple implements Codec {
    protected long id;
    protected String name;
    protected byte flag;
    protected boolean good;
    protected byte[] data;

    public Apple() {
    }

    public Apple(long id, String name, byte flag, boolean good, byte[] data) {
        this.id = id;
        this.name = name;
        this.flag = flag;
        this.good = good;
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getFlag() {
        return flag;
    }

    public void setFlag(byte flag) {
        this.flag = flag;
    }

    public boolean isGood() {
        return good;
    }

    public void setGood(boolean good) {
        this.good = good;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public void encode(final ObjectWriter output) throws IOException {
        output.writeLong(id);
        output.writeString(name);
        output.writeByte((int) flag);
        output.writeBoolean(good);
        output.writeInt(data == null ? -1 : data.length);
        if (data != null && data.length > 0) {
            output.write(data);
        }
    }

    @Override
    public void decode(final ObjectReader input) throws IOException {
        id = input.readLong();
        name = input.readString();
        flag = input.readByte();
        good = input.readBoolean();
        int len = input.readInt();
        if (len >= 0) {
            data = new byte[len];
            if (len > 0) {
                input.read(data);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Apple apple = (Apple) o;

        if (id != apple.id) {
            return false;
        }
        if (flag != apple.flag) {
            return false;
        }
        if (good != apple.good) {
            return false;
        }
        if (name != null ? !name.equals(apple.name) : apple.name != null) {
            return false;
        }
        return Arrays.equals(data, apple.data);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (int) flag;
        result = 31 * result + (good ? 1 : 0);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }
}
