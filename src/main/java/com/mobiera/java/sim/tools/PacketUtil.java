package com.mobiera.java.sim.tools;

public class PacketUtil {

	private static int MIN_PACKET_SIZE = 10;
	
	// check if looks a command packet
	// 027100000E0A00000000000000000000016101
	// 027100000B0A0000000000000000000A
	
	public static boolean isCommandPacket(byte[] msg) {
		
		if (msg.length >= MIN_PACKET_SIZE) {
			// binary msg
			if ( (msg[0] == (byte)0x02) && ( (msg[1] == (byte)0x71) || (msg[1] == (byte)0x70) ) && (msg[2] == (byte)0x00)) {
					// looks like a packet
				
				return true;
		
			}
		}
		
		return false;
	}
	
	public static byte[] getCommandPacketTar(byte[] msg) {
		
		byte[] binTar = new byte[3];
		System.arraycopy(msg, 10, binTar, 0, 3);
		return binTar;
		
	}
	
	public static boolean isEncrypted(byte[] msg) {
		
		if (((msg[6]) & (msg[7])) != 0x00) {
			return true;
		}
		return false;
	}
	
	
}
