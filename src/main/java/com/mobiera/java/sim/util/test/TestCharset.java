package com.mobiera.java.sim.util.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cloudhopper.commons.charset.Charset;
import com.cloudhopper.commons.charset.CharsetUtil;
import com.mobiera.java.sim.tools.BytecodeUtil;
import com.mobiera.java.sim.tools.CharsetException;
import com.mobiera.java.sim.tools.CharsetTechnology;
import com.mobiera.java.sim.util.tlv.ISOUtil;

public class TestCharset {

	public static void main(String[] args) throws CharsetException {
		// TODO Auto-generated method stub

		String test ="Se te dificulta encontrar sitios para recargar cerca de ti?" // \n"
				+ "\n"
				+ "SI: Presiona OK / Aceptar"//\n"
				+ "NO: Presiona Cancelar";
		Charset charset = BytecodeUtil.getIdealCharset(test, CharsetTechnology.STK);
		
		
		Pattern p = Pattern.compile("[^ \\nA-Za-z0-9@£\\$¥èéùìòÇØøÅåΔ_ΦΓΛΩΠΨΣΘΞÆæßÉ!\\\"#¤%&\\(\\)\\*'\\+,\\-\\./:;<=>\\?¡ÄÖÑÜ§¿äöñüà]");
		Matcher m = p.matcher(test);
		boolean b = m.find();

		System.out.println("" + b);
		
		CharsetUtil.encode(test, charset);
		
		System.out.println(charset + " " + ISOUtil.hexString(CharsetUtil.encode(test, charset)));
		
		
	}

}
