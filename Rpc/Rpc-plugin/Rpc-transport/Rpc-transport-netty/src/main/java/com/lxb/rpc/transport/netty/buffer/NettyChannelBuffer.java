package com.lxb.rpc.transport.netty.buffer;

import com.lxb.rpc.transport.buffer.ChannelBuffer;
import com.lxb.rpc.transport.buffer.ChannelBufferInputStream;
import com.lxb.rpc.transport.buffer.ChannelBufferOutputStream;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Netty连接通道缓冲区
 */
public class NettyChannelBuffer implements ChannelBuffer {
    /**
     * 字节缓冲区
     */
    protected final ByteBuf byteBuf;
    /**
     * 释放标识
     */
    protected       boolean released = false;

    public NettyChannelBuffer(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    public ByteBuf getByteBuf() {
        return byteBuf;
    }

    @Override
    public int capacity() {
        return byteBuf.capacity();
    }

    @Override
    public ChannelBuffer capacity(int newCapacity) {
        return new NettyChannelBuffer(byteBuf.capacity(newCapacity));
    }

    @Override
    public void ensureWritable(int minWritableBytes) {
        byteBuf.ensureWritable(minWritableBytes);
    }

    @Override
    public void clear() {
        byteBuf.clear();
    }

    @Override
    public ChannelBuffer copy() {
        return new NettyChannelBuffer(byteBuf.copy());
    }

    @Override
    public ChannelBuffer copy(int index, int length) {
        return new NettyChannelBuffer(byteBuf.copy(index, length));
    }

    @Override
    public void discardReadBytes() {
        byteBuf.discardReadBytes();
    }

    @Override
    public short getUnsignedByte(int index) {
        return byteBuf.getUnsignedByte(index);
    }

    @Override
    public int getUnsignedShort(int var1) {
        return byteBuf.getUnsignedShort(var1);
    }

    @Override
    public byte getByte(int index) {
        return byteBuf.getByte(index);
    }

    @Override
    public short getShort(int index) {
        return byteBuf.getShort(index);
    }

    @Override
    public int getInt(int index) {
        return byteBuf.getInt(index);
    }

    @Override
    public long getLong(int index) {
        return byteBuf.getLong(index);
    }

    @Override
    public ChannelBuffer getBytes(int index, byte[] dst) {
        return new NettyChannelBuffer(byteBuf.getBytes(index, dst));
    }

    @Override
    public ChannelBuffer getBytes(int index, byte[] dst, int dstIndex, int length) {
        return new NettyChannelBuffer(byteBuf.getBytes(index, dst, dstIndex, length));
    }

    @Override
    public ChannelBuffer getBytes(int index, ByteBuffer dst) {
        return new NettyChannelBuffer(byteBuf.getBytes(index, dst));
    }

    @Override
    public ChannelBuffer getBytes(int index, ChannelBuffer dst) {
        return new NettyChannelBuffer(byteBuf.getBytes(index, dst.toByteBuffer()));
    }

    @Override
    public boolean isReadable() {
        return byteBuf.isReadable();
    }

    @Override
    public int readableBytes() {
        return byteBuf.readableBytes();
    }

    @Override
    public byte readByte() {
        return byteBuf.readByte();
    }

    @Override
    public void readBytes(byte[] dst) {
        byteBuf.readBytes(dst);
    }

    @Override
    public void readBytes(byte[] dst, int dstIndex, int length) {
        byteBuf.readBytes(dst, dstIndex, length);
    }

    @Override
    public void readBytes(ChannelBuffer dst) {
        byteBuf.readBytes(dst.toByteBuffer());
    }

    @Override
    public ChannelBuffer readBytes(OutputStream out, int length) throws IOException {
        return new NettyChannelBuffer(byteBuf.readBytes(out, length));
    }

    @Override
    public void resetReaderIndex() {
        byteBuf.resetReaderIndex();
    }

    @Override
    public void resetWriterIndex() {
        byteBuf.resetWriterIndex();
    }

    @Override
    public int readerIndex() {
        return byteBuf.readerIndex();
    }

    @Override
    public void readerIndex(int readerIndex) {
        byteBuf.readerIndex(readerIndex);
    }

    @Override
    public ChannelBuffer readSlice(int length) {
        return new NettyChannelBuffer(byteBuf.readSlice(length));
    }

    @Override
    public void setByte(int index, int value) {
        byteBuf.setByte(index, value);
    }

    @Override
    public void setBytes(int index, byte[] src) {
        byteBuf.setBytes(index, src);
    }

    @Override
    public void setBytes(int index, byte[] src, int srcIndex, int length) {
        byteBuf.setBytes(index, src, srcIndex, length);
    }

    @Override
    public void setBytes(int index, ByteBuffer src) {
        byteBuf.setBytes(index, src);
    }

    @Override
    public void setBytes(int index, ChannelBuffer src) {
        byteBuf.setBytes(index, src.toByteBuffer());
    }

    @Override
    public void setIndex(int readerIndex, int writerIndex) {
        byteBuf.setIndex(readerIndex, writerIndex);
    }

    @Override
    public void skipBytes(int length) {
        byteBuf.skipBytes(length);
    }

    @Override
    public ByteBuffer toByteBuffer() {
        return byteBuf.nioBuffer();
    }

    @Override
    public ByteBuffer toByteBuffer(int index, int length) {
        return byteBuf.nioBuffer(index, length);
    }

    @Override
    public boolean isWritable() {
        return byteBuf.isWritable();
    }

    @Override
    public int writableBytes() {
        return byteBuf.writableBytes();
    }

    @Override
    public void writeByte(int value) {
        byteBuf.writeByte(value);
    }

    @Override
    public void writeBytes(byte[] src) {
        byteBuf.writeBytes(src);
    }

    @Override
    public void writeBytes(byte[] src, int index, int length) {
        byteBuf.writeBytes(src, index, length);
    }

    @Override
    public void writeBytes(ChannelBuffer src) {
        byteBuf.writeBytes(src.toByteBuffer());
    }

    @Override
    public int writeBytes(InputStream in, int length) throws IOException {
        return byteBuf.writeBytes(in, length);
    }

    @Override
    public int writerIndex() {
        return byteBuf.writerIndex();
    }

    @Override
    public void writerIndex(int writerIndex) {
        byteBuf.writerIndex(writerIndex);
    }

    @Override
    public byte[] array() {
        return byteBuf.array();
    }

    @Override
    public boolean hasArray() {
        return byteBuf.hasArray();
    }

    @Override
    public int arrayOffset() {
        return byteBuf.arrayOffset();
    }

    @Override
    public long readLong() {
        return byteBuf.readLong();
    }

    @Override
    public int readInt() {
        return byteBuf.readInt();
    }

    @Override
    public short readShort() {
        return byteBuf.readShort();
    }

    @Override
    public float readFloat() {
        return byteBuf.readFloat();
    }

    @Override
    public double readDouble() {
        return byteBuf.readDouble();
    }

    @Override
    public boolean readBoolean() {
        return byteBuf.readBoolean();
    }

    @Override
    public void writeLong(long value) {
        byteBuf.writeLong(value);
    }

    @Override
    public void writeInt(int value) {
        byteBuf.writeInt(value);
    }

    @Override
    public void writeShort(int value) {
        byteBuf.writeShort(value);
    }

    @Override
    public void writeDouble(double value) {
        byteBuf.writeDouble(value);
    }

    @Override
    public void writeFloat(float value) {
        byteBuf.writeFloat(value);
    }

    @Override
    public void writeBoolean(boolean value) {
        byteBuf.writeBoolean(value);
    }

    @Override
    public void setInt(int index, int value) {
        byteBuf.setInt(index, value);
    }

    @Override
    public void setBoolean(int index, boolean value) {
        byteBuf.setBoolean(index, value);
    }

    @Override
    public void setShort(int index, int value) {
        byteBuf.setShort(index, value);
    }

    @Override
    public void setLong(int index, long value) {
        byteBuf.setLong(index, value);
    }

    @Override
    public void setFloat(int index, float value) {
        byteBuf.setFloat(index, value);
    }

    @Override
    public void setDouble(int index, double value) {
        byteBuf.setDouble(index, value);
    }

    @Override
    public boolean release() {
        return released = byteBuf.release();
    }

    @Override
    public boolean isReleased() {
        return released;
    }

    @Override
    public InputStream inputStream() {
        return new ChannelBufferInputStream(this);
    }

    @Override
    public InputStream inputStream(int length) {
        return new ChannelBufferInputStream(this, length);
    }

    @Override
    public OutputStream outputStream() {
        return new ChannelBufferOutputStream(this);
    }
}
