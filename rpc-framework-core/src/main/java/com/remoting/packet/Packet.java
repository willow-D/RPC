package com.remoting.packet;

import lombok.Data;

import java.io.Serializable;

@Data
public abstract class Packet implements Serializable {

    private Byte version = 1;

    public abstract Byte getCommand();
}
