package com.mobiera.java.sim.util.tlv;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.mobiera.java.sim.util.tlv.BerTlv;
import com.mobiera.java.sim.util.tlv.ISOUtil;
import com.mobiera.java.sim.util.tlv.Tag;

class PrimitiveBerTlv extends BerTlv implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7868954338547187697L;
	private final byte[] value;

    PrimitiveBerTlv(Tag tag, byte[] value) {
        super(tag);
        this.value = value;
    }

    public byte[] getValue() {
        return value;
    }

    public BerTlv findTlv(Tag tag) {
        return null;
    }

    public List<BerTlv> findTlvs(Tag tag) {
        return Collections.emptyList();
    }

    public List<BerTlv> getChildren() {
        return Collections.emptyList();
    }

    public String toString() {
        return getTag() + ": " + ISOUtil.hexString(value);
    }
}
