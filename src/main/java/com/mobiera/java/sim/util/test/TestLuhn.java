package com.mobiera.java.sim.util.test;

import com.mobiera.java.sim.tools.BytecodeUtil;
import com.mobiera.java.sim.util.tlv.ISOUtil;

public class TestLuhn {

	public static void main(String[] args) {
		
		String siccid="981501310300001086F0";
		
		
		String iccid = BytecodeUtil.getIccidFromBytes(ISOUtil.hex2byte(siccid));
				
				
				//"8951150002506565263";
		
		
		if (iccid.length() == 19) {
			iccid = iccid + BytecodeUtil.calculateLuhnNumber(iccid);
			System.out.println(iccid);
		} else if (iccid.length() == 20){
			System.out.println(iccid);

		}
		
		
		
	}

}
