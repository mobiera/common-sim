package com.mobiera.java.sim.tools;

/**
 * Helpers for GSM 03.48 / ETSI TS 102 225 secured packets carried in SMS user data.
 *
 * Both packet kinds start with a 3 byte user data header: UDHL (02), IEI, IEDL (00).
 * IEI 0x70 is a command packet, IEI 0x71 a response packet (Proof of Receipt).
 *
 * <pre>
 * command packet  : UDH(3) CPL(2) CHL(1) SPI(2) KIc(1) KID(1) TAR(3) CNTR(5) PCNTR(1) ...
 * response packet : UDH(3) RPL(2) RHL(1) TAR(3) CNTR(5) PCNTR(1) STATUS(1) ...
 * </pre>
 */
public class PacketUtil {

	private static int MIN_PACKET_SIZE = 10;

	public static final byte IEI_COMMAND_PACKET = (byte) 0x70;
	public static final byte IEI_RESPONSE_PACKET = (byte) 0x71;

	public static final int UDH_LENGTH = 3;
	public static final int COMMAND_PACKET_TAR_OFFSET = UDH_LENGTH + 2 + 1 + 2 + 1 + 1; // 10
	public static final int RESPONSE_PACKET_TAR_OFFSET = UDH_LENGTH + 2 + 1;             // 6
	public static final int TAR_LENGTH = 3;

	// check if looks a command packet
	// 027100000E0A00000000000000000000016101
	// 027100000B0A0000000000000000000A

	/**
	 * True when the message starts with a 03.48 user data header, either command (0x70)
	 * or response (0x71) packet. Kept for backward compatibility: despite its name it
	 * accepts both kinds.
	 */
	public static boolean isCommandPacket(byte[] msg) {

		if (msg != null && msg.length >= MIN_PACKET_SIZE) {
			// binary msg
			if ( (msg[0] == (byte)0x02) && ( (msg[1] == IEI_RESPONSE_PACKET) || (msg[1] == IEI_COMMAND_PACKET) ) && (msg[2] == (byte)0x00)) {
					// looks like a packet

				return true;

			}
		}

		return false;
	}

	/**
	 * True when the message is a 03.48 response packet (Proof of Receipt), IEI 0x71.
	 */
	public static boolean isResponsePacket(byte[] msg) {
		return isCommandPacket(msg) && msg[1] == IEI_RESPONSE_PACKET;
	}

	/**
	 * TAR of a command packet (offset 10, after the security parameters). Do not use on a
	 * response packet, see {@link #getPacketTar(byte[])}.
	 */
	public static byte[] getCommandPacketTar(byte[] msg) {

		byte[] binTar = new byte[TAR_LENGTH];
		System.arraycopy(msg, COMMAND_PACKET_TAR_OFFSET, binTar, 0, TAR_LENGTH);
		return binTar;

	}

	/**
	 * TAR of a response packet (offset 6, right after RPL and RHL).
	 */
	public static byte[] getResponsePacketTar(byte[] msg) {

		byte[] binTar = new byte[TAR_LENGTH];
		System.arraycopy(msg, RESPONSE_PACKET_TAR_OFFSET, binTar, 0, TAR_LENGTH);
		return binTar;

	}

	/**
	 * TAR of either packet kind, chosen from the UDH IEI. Returns null when the message is
	 * not a packet or is too short to hold a TAR.
	 */
	public static byte[] getPacketTar(byte[] msg) {

		if (!isCommandPacket(msg)) return null;

		int offset = (msg[1] == IEI_RESPONSE_PACKET) ? RESPONSE_PACKET_TAR_OFFSET : COMMAND_PACKET_TAR_OFFSET;
		if (msg.length < offset + TAR_LENGTH) return null;

		byte[] binTar = new byte[TAR_LENGTH];
		System.arraycopy(msg, offset, binTar, 0, TAR_LENGTH);
		return binTar;
	}

	/**
	 * Packet without its user data header, i.e. starting at CPL or RPL. Returns the
	 * message itself when it does not carry a 03.48 UDH.
	 */
	public static byte[] stripUdh(byte[] msg) {
		if (!isCommandPacket(msg)) return msg;
		byte[] out = new byte[msg.length - UDH_LENGTH];
		System.arraycopy(msg, UDH_LENGTH, out, 0, out.length);
		return out;
	}

	public static boolean isEncrypted(byte[] msg) {

		if (((msg[6]) & (msg[7])) != 0x00) {
			return true;
		}
		return false;
	}


}
