package com.lxb.rpc.serealization.model;



import java.io.Serializable;

public class TransientObj implements Serializable {

    private int id;

    private transient int type;

    public TransientObj() {
    }

    public TransientObj(int id, int type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TransientObj that = (TransientObj) o;

        return id == that.id && type == that.type;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
