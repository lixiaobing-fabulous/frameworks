package com.lxb.rpc.serealization.model;


public class PhoneNumber {

    private String number;
    private PhoneType type = PhoneType.HOME;

    public PhoneNumber() {
    }

    public PhoneNumber(String number, PhoneType type) {
        this.number = number;
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public PhoneType getType() {
        return type;
    }

    public void setType(PhoneType type) {
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

        PhoneNumber that = (PhoneNumber) o;

        if (number != null ? !number.equals(that.number) : that.number != null) {
            return false;
        }
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = number != null ? number.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
