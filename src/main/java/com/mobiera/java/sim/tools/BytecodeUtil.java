package com.mobiera.java.sim.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudhopper.commons.charset.Charset;
import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.smpp.SmppConstants;
import com.mobiera.commons.exception.NotSupportedDataLengthException;
import com.mobiera.java.sim.util.tlv.ISOUtil;

public class BytecodeUtil {

	/*
	public static final byte DATA_CODING_DEFAULT 	= (byte)0x00;	// SMSC Default Alphabet
    public static final byte DATA_CODING_IA5		= (byte)0x01;	// IA5 (CCITT T.50)/ASCII (ANSI X3.4)
    public static final byte DATA_CODING_8BITA		= (byte)0x02;	// Octet unspecified (8-bit binary) defined for TDMA and/ or CDMA but not defined for GSM
    public static final byte DATA_CODING_LATIN1		= (byte)0x03;	// Latin 1 (ISO-8859-1)
    public static final byte DATA_CODING_8BIT		= (byte)0x04;	// Octet unspecified (8-bit binary) ALL TECHNOLOGIES
    public static final byte DATA_CODING_JIS		= (byte)0x05;	// JIS (X 0208-1990)
    public static final byte DATA_CODING_CYRLLIC	= (byte)0x06;	// Cyrllic (ISO-8859-5)
    public static final byte DATA_CODING_HEBREW		= (byte)0x07;	// Latin/Hebrew (ISO-8859-8)
    public static final byte DATA_CODING_UCS2		= (byte)0x08;	// UCS2 (ISO/IEC-10646)
    public static final byte DATA_CODING_PICTO		= (byte)0x09;	// Pictogram Encoding
    public static final byte DATA_CODING_MUSIC		= (byte)0x0A;	// ISO-2022-JP (Music Codes)
    public static final byte DATA_CODING_RSRVD		= (byte)0x0B;	// reserved
    public static final byte DATA_CODING_RSRVD2		= (byte)0x0C;	// reserved
    public static final byte DATA_CODING_EXKANJI	= (byte)0x0D;	// Extended Kanji JIS(X 0212-1990)
    public static final byte DATA_CODING_KSC5601	= (byte)0x0E;	// KS C 5601
    public static final byte DATA_CODING_RSRVD3		= (byte)0x0F;	// reserved
*/
	
	
	private static Logger logger = LoggerFactory.getLogger(BytecodeUtil.class);

    public static byte[] getTextBytecode(String text, Charset charset, boolean debug) throws CharsetException, IOException {
    	
    	if (charset == null) {
    		throw new CharsetException("getTextBytecode: Charset cannot be null");
    	}
    	
    	if ( 
    			(charset.equals(CharsetUtil.CHARSET_GSM)) ||
    			(charset.equals(CharsetUtil.CHARSET_PACKED_GSM)) ||
    			(charset.equals(CharsetUtil.CHARSET_ISO_8859_1)) ||
    			(charset.equals(CharsetUtil.CHARSET_UCS_2)) ) {
    		
    	} else {
    		throw (new CharsetException("getTextBytecode: Unsupported Charset : " + charset));
    	}
    	
    	
		byte[] byteCode =  CharsetUtil.encode(text, charset);
		
		if (debug) printDebug("getDataBytecode " + text, byteCode);
		
		return byteCode;
    }

    
    
    public static Byte getSmscDcsForCharset(Charset charset, boolean simTechnology) throws CharsetException {
    	
    	if (charset == null) throw new CharsetException("getSmscDcsForCharset: Charset cannot be null");
    	
    	if (simTechnology) {
    		if (charset.equals(CharsetUtil.CHARSET_GSM)) {
        		return SmppConstants.DATA_CODING_8BIT;
        	} else {
        		if (charset.equals(CharsetUtil.CHARSET_UCS_2)) {
            		return SmppConstants.DATA_CODING_UCS2;
            	} else {
            		if (charset.equals(CharsetUtil.CHARSET_PACKED_GSM)) {
                		return SmppConstants.DATA_CODING_DEFAULT;
                	} else {
                		throw new CharsetException("getSmscDcsForCharset: Unsupported charset for SIM technology: " + charset);
                	}
            	}
        	}
    	} else {
    		
    		if (charset.equals(CharsetUtil.CHARSET_GSM)) {
        		return SmppConstants.DATA_CODING_DEFAULT;
        	} else {
        		if (charset.equals(CharsetUtil.CHARSET_UCS_2)) {
            		return SmppConstants.DATA_CODING_UCS2;
            	} else {
            		if (charset.equals(CharsetUtil.CHARSET_ISO_8859_1)) {
                		return SmppConstants.DATA_CODING_LATIN1;
                	} else {
                		if (charset.equals(CharsetUtil.CHARSET_PACKED_GSM)) {
                    		return SmppConstants.DATA_CODING_DEFAULT;
                    	} else {
                    		throw new CharsetException("getSmscDcsForCharset: Unsupported charset: " + charset + " please add me");
                    	}
                	}
            	}
        	}
    	}
    	
    	
        
    }
    protected byte[] concatenate(byte[] b1, byte[] b2) {
		byte[] res = new byte[b1.length + b2.length];
		int index = 0;
		System.arraycopy(b1, 0, res, index, b1.length);
		index+=b1.length;
		System.arraycopy(b2, 0, res, index, b2.length);
		return res;
		
	}
	
	public static byte[] concatenate(List<byte[]> list) {
		
		int totalSize = 0;
		int index = 0;
		
		for (Iterator<byte[]> i = list.iterator(); i.hasNext();) {
			byte[] current = i.next();
			totalSize += current.length;
		}
		
		byte[] res = new byte[totalSize];
		
		for (Iterator<byte[]> i = list.iterator(); i.hasNext();) {
			byte[] current = i.next();
			System.arraycopy(current, 0, res, index, current.length);
			index+=current.length;
		}
		
		
		return res;
		
	}
	
	
	public static void printDebug(String name, byte[] bytecode) {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append(" @@@ ");
		
	    for (byte b : bytecode) {
	        sb.append(String.format("%02X ", b));
	    }
	    logger.debug(sb.toString());
		
	}

	public static Charset getIdealCharset(String s, CharsetTechnology charsetTechnology) throws CharsetException {
		
		/*
		 * use PLAINTEXT for standard text SMS
		 */
		boolean packedGsmCompatible = false;
		boolean gsmCompatible = false;
		boolean latin1Compatible = false;
		boolean ucs2Compatible = false;
		
		if (charsetTechnology.equals(CharsetTechnology.SMSC)) {
			packedGsmCompatible = true;
			latin1Compatible = true;
			ucs2Compatible = true;
		} else if (charsetTechnology.equals(CharsetTechnology.SMSC_NO_PACKED_GSM)) {
			gsmCompatible = true;
			latin1Compatible = true;
			ucs2Compatible = true;
		} else if (charsetTechnology.equals(CharsetTechnology.SMSC_GSM_UCS2_ONLY)) {
			gsmCompatible = true;
			ucs2Compatible = true;
		} else if (charsetTechnology.equals(CharsetTechnology.SMSC_PACKED_GSM_UCS2_ONLY)) {
			packedGsmCompatible = true;
			ucs2Compatible = true;
		} else if (charsetTechnology.equals(CharsetTechnology.SMSC_PACKED_GSM_ONLY)) {
			packedGsmCompatible = true;
		} else if (charsetTechnology.equals(CharsetTechnology.SMSC_GSM_ONLY)) {
			gsmCompatible = true;
		} else if (charsetTechnology.equals(CharsetTechnology.SMSC_NO_UCS2)) {
			packedGsmCompatible = true;
			latin1Compatible = true;
		} else if (charsetTechnology.equals(CharsetTechnology.STK)) {
			packedGsmCompatible = true;
			gsmCompatible = true;
			ucs2Compatible = true;
		} else if (charsetTechnology.equals(CharsetTechnology.STK_NO_PACKED_GSM)) {
			gsmCompatible = true;
			ucs2Compatible = true;
		} else if (charsetTechnology.equals(CharsetTechnology.STK_GSM_ONLY)) {
			gsmCompatible = true;
		}else if (charsetTechnology.equals(CharsetTechnology.STK_UCS2_ONLY)) {
			ucs2Compatible = true;
		}
		
		/*
		 * SMSC_PACKED_GSM_ONLY,
	SMSC_GSM_ONLY,
	SMSC_NO_UCS2
		 */
		Charset chosenCharset = CharsetUtil.CHARSET_GSM;
		if (packedGsmCompatible) chosenCharset = CharsetUtil.CHARSET_PACKED_GSM;
		
		if (s == null || s.trim().isEmpty()) {
			return chosenCharset;
		}
		
		// to escape in regex : \.[]{}()*+-?^$|

		/*
		 * Check for GSM chars
		 */

		Pattern p = Pattern.compile("[^ \\nA-Za-z0-9@£\\$¥èéùìòÇØøÅåΔ_ΦΓΛΩΠΨΣΘΞÆæßÉ!\\\"#¤%&\\(\\)\\*'\\+,\\-\\./:;<=>\\?¡ÄÖÑÜ§¿äöñüà]");
		Matcher m = p.matcher(s);
		boolean b = m.find();

		if (b == true) {
			
			// detected non-gsm char
			// special no GSM char found, trying Latin1
			if (latin1Compatible) {
				p = Pattern.compile("[^ \\nA-Za-z0-9@='!\"#\\$%&\\(\\)\\*\\+,\\-\\./:;<>?@\\[\\]\\^_`\\{|\\}~€‚ƒ„…†‡ˆ‰Š‹ŒŽ‘’“”•–—˜™š›œžŸ¡¢£¤¥\\|§¨©ª«¬®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿ]");
				m = p.matcher(s);
				b = m.find();

				if (b == true) {
					// no Latin1 char, using UCS2
					if (ucs2Compatible) {
						chosenCharset = CharsetUtil.CHARSET_UCS_2;
					} else {
						throw new CharsetException("No compatible charset found for this text");
					}
					
				} else {
					// using latin1
					chosenCharset = CharsetUtil.CHARSET_ISO_8859_1;
				}
			} else {
				if (ucs2Compatible) {
					chosenCharset = CharsetUtil.CHARSET_UCS_2;
				} else {
					throw new CharsetException("No compatible charset found for this text");
				}
			}
			

		} 

		return chosenCharset;


	}
	
	public static Charset getIdealCharset(String s, List<Charset> allowedCharsets, boolean simTechnology) throws CharsetException {
		
		/*
		 * use PLAINTEXT for standard text SMS
		 */
		boolean packedGsmCompatible = false;
		boolean gsmCompatible = false;
		boolean latin1Compatible = false;
		boolean ucs2Compatible = false;
		
		if (allowedCharsets.contains(CharsetUtil.CHARSET_PACKED_GSM)) packedGsmCompatible = true;
		if (allowedCharsets.contains(CharsetUtil.CHARSET_GSM)) gsmCompatible = true;
		if (!simTechnology && allowedCharsets.contains(CharsetUtil.CHARSET_ISO_8859_1)) latin1Compatible = true;
		if (allowedCharsets.contains(CharsetUtil.CHARSET_UCS_2)) ucs2Compatible = true;
		
		if (!(packedGsmCompatible||gsmCompatible||latin1Compatible||ucs2Compatible)) 
			throw new CharsetException("at least one of CHARSET_PACKED_GSM, CHARSET_GSM, CHARSET_UCS_2, CHARSET_ISO_8859_1");
		
		Charset chosenCharset = CharsetUtil.CHARSET_GSM;
		if (packedGsmCompatible) chosenCharset = CharsetUtil.CHARSET_PACKED_GSM;
		
		if (s == null || s.trim().isEmpty()) {
			return chosenCharset;
		}
		
		// to escape in regex : \.[]{}()*+-?^$|

		/*
		 * Check for GSM chars
		 */

		Pattern p = Pattern.compile("[^ \\nA-Za-z0-9@£\\$¥èéùìòÇØøÅåΔ_ΦΓΛΩΠΨΣΘΞÆæßÉ!\\\"#¤%&\\(\\)\\*'\\+,\\-\\./:;<=>\\?¡ÄÖÑÜ§¿äöñüà]");
		Matcher m = p.matcher(s);
		boolean b = m.find();

		if (b == true) {
			
			// detected non-gsm char
			// special no GSM char found, trying Latin1
			if (latin1Compatible) {
				p = Pattern.compile("[^ \\nA-Za-z0-9@='!\"#\\$%&\\(\\)\\*\\+,\\-\\./:;<>?@\\[\\]\\^_`\\{|\\}~€‚ƒ„…†‡ˆ‰Š‹ŒŽ‘’“”•–—˜™š›œžŸ¡¢£¤¥\\|§¨©ª«¬®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿ]");
				m = p.matcher(s);
				b = m.find();

				if (b == true) {
					// no Latin1 char, using UCS2
					if (ucs2Compatible) {
						chosenCharset = CharsetUtil.CHARSET_UCS_2;
					} else {
						throw new CharsetException("No compatible charset found for this text");
					}
					
				} else {
					// using latin1
					chosenCharset = CharsetUtil.CHARSET_ISO_8859_1;
				}
			} else {
				if (ucs2Compatible) {
					chosenCharset = CharsetUtil.CHARSET_UCS_2;
				} else {
					throw new CharsetException("No compatible charset found for this text");
				}
			}
			

		} 

		return chosenCharset;


	}
	public static  String getImeiFromBytes(byte[] binaryInfo) throws IOException {
		if (binaryInfo.length != 8) throw new IOException("IMEI: Invalid length must be 8");
		
		String text = javax.xml.bind.DatatypeConverter.printHexBinary(binaryInfo);
		
		StringBuffer fixedText = new StringBuffer(15);
		
		fixedText.append(text.substring(0, 1));
		for (int i=2; i<13; i+=2) {
			fixedText.append(text.substring(i+1, i+2));
			fixedText.append(text.substring(i, i+1));
			
		}
		fixedText.append(text.substring(15, 16));
		
		String imei = fixedText.toString();
		//System.out.println("IMEI : " + imei);
		
		
		 
		 return imei + calculateLuhnNumber(imei);
		 
		
	}

	
	public static int calculateLuhnNumber(String data) {
		
		
		int index = 1;
		 int sum = 0;
		 for (int i=data.length()-1; i>=0; i--) {
			 if ( (index%2) == 0) {
				 //System.out.println("1x" + imei.substring(i, i+1) + " => " + imei.substring(i, i+1));
				 sum += 1 * (Integer.parseInt(data.substring(i, i+1)));
			 } else {
				 int doubled = 2 * (Integer.parseInt(data.substring(i, i+1)));
				 if (doubled >= 10) {
					 String doubledString = new String("" + doubled);
					 int doubledSum = Integer.parseInt(doubledString.substring(0,1)) + Integer.parseInt(doubledString.substring(1,2));
					 
					 sum += doubledSum;
					 
				 } else {
					 //System.out.println("2x"  +  imei.substring(i, i+1) + doubled );
					 sum += doubled;
				 }
				 
			 }
			 index ++;
			 //System.out.println("Sum " + sum);
		 }
		 
		 int result = (10 - (sum % 10)) % 10;
		 
		 
		 return result;
		 
		
	}
	
	public static CellLocation getLocationFromBytes(byte[] locParamBytes) {
		
		
		/*
		 * 
		 * Fix using info in documentation/CellLocation/
		 * 
		 * Download database here : http://opencellid.org/
		 * 
		 */
		
		CellLocation vo = new CellLocation();
		byte[] mccmnc = Arrays.copyOfRange(locParamBytes, 0, 4);
		byte[] lactac = Arrays.copyOfRange(locParamBytes, 4, 6);
		byte[] cellid = Arrays.copyOfRange(locParamBytes, 6, locParamBytes.length);
		
		
		String mccMncStr = javax.xml.bind.DatatypeConverter.printHexBinary(mccmnc);
		StringBuffer mccString = new StringBuffer(3);
		StringBuffer mncString = new StringBuffer(3);
		mccString.append(mccMncStr.charAt(1));
		mccString.append(mccMncStr.charAt(0));
		mccString.append(mccMncStr.charAt(3));
		
		if (mccMncStr.charAt(2) != 'F') mncString.append(mccMncStr.charAt(2));
		mncString.append(mccMncStr.charAt(4));
		mncString.append(mccMncStr.charAt(5));
		
		vo.setMcc(Integer.parseInt(mccString.toString()));
		vo.setMnc(Integer.parseInt(mncString.toString()));
		
		
		vo.setLacTac(Long.parseLong(javax.xml.bind.DatatypeConverter.printHexBinary(lactac), 16));
		
		if (cellid.length == 2) {
			vo.setCellId(Long.parseLong(javax.xml.bind.DatatypeConverter.printHexBinary(cellid), 16));
			
		} else {
			String cellIdStr = javax.xml.bind.DatatypeConverter.printHexBinary(cellid);
			if (cellIdStr.endsWith("F")) cellIdStr = cellIdStr.substring(0, cellIdStr.length()-1);
			vo.setCellId(Long.parseLong(cellIdStr, 16));
		}
		
		return vo;
	}


	public static void validateSupportedCharset(Charset charset, CharsetTechnology charsetTechnology) throws CharsetException {
		
		boolean packedGsmCompatible = false;
		boolean gsmCompatible = false;
		boolean latin1Compatible = false;
		boolean ucs2Compatible = false;
		
		
		if (charsetTechnology.equals(CharsetTechnology.SMSC)) {
			packedGsmCompatible = true;
			gsmCompatible = true;
			latin1Compatible = true;
			ucs2Compatible = true;
		} else if (charsetTechnology.equals(CharsetTechnology.SMSC_NO_PACKED_GSM)) {
			gsmCompatible = true;
			latin1Compatible = true;
			ucs2Compatible = true;
		} else if (charsetTechnology.equals(CharsetTechnology.SMSC_GSM_UCS2_ONLY)) {
			gsmCompatible = true;
			ucs2Compatible = true;
		} else if (charsetTechnology.equals(CharsetTechnology.SMSC_PACKED_GSM_UCS2_ONLY)) {
			packedGsmCompatible = true;
			ucs2Compatible = true;
		} else if (charsetTechnology.equals(CharsetTechnology.STK)) {
			packedGsmCompatible = true;
			gsmCompatible = true;
			ucs2Compatible = true;
		} else if (charsetTechnology.equals(CharsetTechnology.STK_GSM_ONLY)) {
			packedGsmCompatible = false;
			gsmCompatible = true;
			ucs2Compatible = false;
		} 
		
		
		
		if (gsmCompatible && charset.equals(CharsetUtil.CHARSET_GSM)) {
    		return;
    	} else {
    		if (ucs2Compatible && charset.equals(CharsetUtil.CHARSET_UCS_2)) {
        		return;
        	} else {
        		if (latin1Compatible && charset.equals(CharsetUtil.CHARSET_ISO_8859_1)) {
            		return;
            	} else {
            		if (packedGsmCompatible && charset.equals(CharsetUtil.CHARSET_PACKED_GSM)) {
                		return;
                	} else {
                		throw new CharsetException("validateSupportedCharset: Unsupported charset " + charset);
                	}
            	}
        	}
    	}
	}
	
	
	
	public static byte [] msisdnTo0340AddressField(String msisdn, boolean debug) throws IOException, CharsetException {
		byte [] output = new byte[0];
		
		
		if (msisdn != null) {
			// Default TON/NPI 0x81 (unknown)
			byte tonnpi = get0340TonNpi(msisdn, debug);
			
			if (tonnpi == (byte)0xD1) {
				byte[] codedMsisdn = getTextBytecode(msisdn, CharsetUtil.CHARSET_GSM7, debug);
				output = new byte[2 + codedMsisdn.length];
				output[0] = (byte) (codedMsisdn.length * 2); // semi octets 
				output[1] = tonnpi;
				System.arraycopy(codedMsisdn, 0, output, 2, codedMsisdn.length);		
			} else {
				byte [] value = ISOUtil.padAndSwapNibbles(msisdn);

				output = new byte[2 + value.length];
				output[0] = (byte) msisdn.length(); // digits
				output[1] = tonnpi;

				System.arraycopy(value, 0, output, 2, value.length);				
			}
		}
		return output;
	}
	
	public static byte get0340TonNpi(String msisdn, boolean debug) throws IOException {
		// Default TON/NPI 0x81 (unknown)
			byte tonnpi = (byte)0x81;
			// Force national numbering TON/NPI (0xA1)
			if (!msisdn.matches("\\d+")) {
				if (msisdn.matches("N\\d+")) {
					msisdn = msisdn.replace("N", "");
					tonnpi = (byte)0xA1;
				} else if (msisdn.matches("^[a-zA-Z0-9]*$")) {
					// Alphanumeric TON/NPI (0xA1)
					tonnpi = (byte)0xD1;
				} else if (msisdn.matches("\\+\\d+")) {
					msisdn = msisdn.replace("+", "");
					tonnpi = (byte)0x91;
				} 
			} else if (msisdn.matches("00\\d+")) {
				msisdn = msisdn.replace("00", "");
				tonnpi = (byte)0x91;
			}
			return tonnpi;
	}
	
	public static byte[] getNumberToCallBytecode(String numberToCall, boolean debug) {
		int length = numberToCall.length();
		int byteLength = 0;
		if ((length % 2 > 0)) {
			byteLength = (( (length -1) / 2 ) + 1);
		}
		
		else byteLength = length / 2;
		byte[] byteCode = new byte[byteLength];
		
		byte currentByte = 0x00;
		int byteIndex = 0;
		int i;
		boolean currentByteOk = true;
		
		for (i=0; i<length; i++) {
			
			String currentStr = numberToCall.substring(i, i+1);
			if (currentStr.equals("*")) {
				if ( (i % 2 == 0) ) {
					currentByte = 0x0A;
					currentByteOk = false;
				} else {
					currentByte += 0xA0;
					byteCode[byteIndex] = currentByte;
					byteIndex++;
					currentByteOk = true;
				}
			} else {
				if (currentStr.equals("#")) {
					if ( (i % 2 == 0) ) {
						currentByte = 0x0B;
						currentByteOk = false;
					} else {
						currentByte += 0xB0;
						byteCode[byteIndex] = currentByte;
						byteIndex++;
						currentByteOk = true;
					}
				} else {
					if ( (i % 2 == 0) ) {
						currentByte = Byte.parseByte(currentStr);
						currentByteOk = false;
					} else {
						currentByte += (Byte.parseByte(currentStr)) * 0x10;
						byteCode[byteIndex] = currentByte;
						byteIndex++;
						currentByteOk = true;
					}
				}
				
			}
			
			
			
		}
		if (!currentByteOk) {
			currentByte += (byte)0xF0;
			byteCode[byteIndex] = currentByte;
		}
		if (debug) printDebug("getNumberToCallBytecode " + numberToCall, byteCode);
		return byteCode;
		
	}
	
	public static String getImsiFromBytes(byte[] bytes) {
		String dec = 
				ISOUtil.swapNibblesAndUnpad(bytes);
		return dec.substring(3, dec.length());
		
	}
	
	public static String getIccidFromBytes(byte[] value) {
    	return ISOUtil.swapNibblesAndUnpad(value);
    }
	
	
	public static List<byte[]> splitFrame(byte[] frame, int chunkSize, int firstPadSize, int nextPadSize)  {
		
		int frameSize = frame.length;
        int left = frameSize;
        int begin = 0;
        int toCopy = 0;
        int chunkAvailable = 0;
        int padSize = 0;
        int currentChunkSize = chunkSize;
        List<byte[]> byteArrays = new ArrayList<byte[]>();
        if (chunkSize == 0) {
            return byteArrays;
        }
        
        
        while (left > 0) {
        	
        	
        	if (begin == 0) {
        		padSize = firstPadSize;
        	} else {
        		padSize = nextPadSize;
        	}
        	chunkAvailable = chunkSize - padSize;
        	
        	if (left >  chunkAvailable) {
        		toCopy = chunkAvailable;
        	} else {
        		toCopy = left;
        		currentChunkSize = padSize + left;
        	}
        	byte[] currentChunk = new byte[currentChunkSize];
        	System.arraycopy(frame, begin, currentChunk, padSize, toCopy);
        	byteArrays.add(currentChunk);
        	
        	left = left - toCopy;
        	begin = begin + toCopy;
        }
        
        
        return byteArrays;
    }
	

	public static String getIccidDB(String iccid) throws NotSupportedDataLengthException {
	
		return getIccidLN(iccid);
	}
	
	public static String getIccidLN(String iccid) throws NotSupportedDataLengthException {
		if (iccid.length() == 19) {
			return (iccid + BytecodeUtil.calculateLuhnNumber(iccid));
		} else if (iccid.length() == 20) {
			iccid = iccid.substring(0,19);
			return (iccid + BytecodeUtil.calculateLuhnNumber(iccid));
			
		} else {
			throw new NotSupportedDataLengthException("getIccidLN: invalid iccid: " + iccid);
		}
	}
	
	public static String getIccidNLN(String iccid) throws NotSupportedDataLengthException {
		if (iccid.length() == 19) {
			return iccid;
		} else if (iccid.length() == 20) {
			return iccid.substring(0, 19);
		} else {
			throw new NotSupportedDataLengthException("getIccidNLN: invalid iccid: " + iccid);
		}
	}
	
	public static String getIccidFLN(String iccid) throws NotSupportedDataLengthException {
		if (iccid.length() == 19) {
			return (iccid + "F");
		} else if (iccid.length() == 20) {
			return (iccid.substring(0, 19) + "F");
		} else {
			throw new NotSupportedDataLengthException("getIccidFLN: invalid iccid: " + iccid);
		}
	}

	
}
