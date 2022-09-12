package com.lxb.rpc.serealization;


import com.lxb.rpc.serealization.model.PhoneType;

public interface HelloGrpc {

    void hello(String phone, PhoneType type);
}
