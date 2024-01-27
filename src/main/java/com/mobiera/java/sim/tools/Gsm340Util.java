package com.mobiera.java.sim.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class Gsm340Util {

	
	
	private static final int SMS_MAX_LENGTH = 140;
	private static final int FIRST_UDH_PADSIZE = 8;
	private static final int NEXT_UDH_PADSIZE = 6;
	private static final int UDH_UNIQUE_PADSIZE = 3;
	
	private static final byte firstUdhNoConc = 0x02;
	
	
	private static final byte firstUdh1 = 0x07; //len of udh
	private static final byte firstUdh2 = 0x00; // concar sms
	private static final byte firstUdh3 = 0x03; // len
	private static final byte firstUdh4 = 0x01; // CONCAT SMS REFERENCE NUMBER
	//byte firstUdh5 = 0x03; // MAXIMUM NUMBER OF SMS
	private static final byte firstUdh6 = 0x01; // SEQ NUMBER OF CURRENT
	
	private static final byte otherUdh1 = 0x05;
	private static final byte otherUdh2 = 0x00;
	private static final byte otherUdh3 = 0x03;
	//private static final byte otherUdh4 = 0x01;
	private static final byte otherUdh5 = 0x03;
	private static final byte firstUdh7 = 0x70; // SIM TOOLKIT SEC HEADERS
	private static final byte firstUdh8 = 0x00; 
	

	
	public static List<byte[]> buildSimMTs(byte[] frame, byte sequence) {
		
		List<byte[]> MTs = null;
		int frameSize = frame.length;
		if (frameSize <= (SMS_MAX_LENGTH - UDH_UNIQUE_PADSIZE ) ) { // size require solo 1 MT
			MTs = splitFrame(frame, SMS_MAX_LENGTH, 
					UDH_UNIQUE_PADSIZE,
					UDH_UNIQUE_PADSIZE );
		}else {
			MTs = splitFrame(frame, SMS_MAX_LENGTH, 
					FIRST_UDH_PADSIZE,
					NEXT_UDH_PADSIZE );
		}	
		int totalMTs = MTs.size();
		
		for (int i=0; i<totalMTs; i++) {
			byte[] currentChunck = MTs.get(i);
			int index=0;
			/*
			 * udh
			 */
			if (totalMTs == 1) {
				currentChunck[index++] = firstUdhNoConc;
				currentChunck[index++] = firstUdh7;
				currentChunck[index++] = firstUdh8;
			} else {
				if (i==0) {
					currentChunck[index++] = firstUdh1;
					currentChunck[index++] = firstUdh2;
					currentChunck[index++] = firstUdh3;
					currentChunck[index++] = sequence;
					currentChunck[index++] = (byte)totalMTs;
					currentChunck[index++] = firstUdh6;
					currentChunck[index++] = firstUdh7;
					currentChunck[index++] = firstUdh8;
					
				} else {
					currentChunck[index++] = otherUdh1;
					currentChunck[index++] = otherUdh2;
					currentChunck[index++] = otherUdh3;
					currentChunck[index++] = sequence;
					currentChunck[index++] = (byte)totalMTs;
					currentChunck[index++] = (byte)(i+1);
				}
			}
		}
		return MTs;
	}
	
	
	public static List<byte[]> buildTextMTs(byte[] frame, byte sequence) {
		
		List<byte[]> MTs = null;
		int frameSize = frame.length;
		if (frameSize <= (SMS_MAX_LENGTH - UDH_UNIQUE_PADSIZE ) ) { // size require solo 1 MT
			MTs = splitFrame(frame, SMS_MAX_LENGTH, 
					UDH_UNIQUE_PADSIZE,
					UDH_UNIQUE_PADSIZE );
		}else {
			MTs = splitFrame(frame, SMS_MAX_LENGTH, 
					NEXT_UDH_PADSIZE,
					NEXT_UDH_PADSIZE );
		}	
		int totalMTs = MTs.size();
		
		for (int i=0; i<totalMTs; i++) {
			byte[] currentChunck = MTs.get(i);
			int index=0;
			/*
			 * udh
			 */
			if (totalMTs == 1) {
				currentChunck[index++] = firstUdhNoConc;
				currentChunck[index++] = firstUdh7;
				currentChunck[index++] = firstUdh8;
			} else {
					currentChunck[index++] = otherUdh1;
					currentChunck[index++] = otherUdh2;
					currentChunck[index++] = otherUdh3;
					currentChunck[index++] = sequence;
					currentChunck[index++] = (byte)totalMTs;
					currentChunck[index++] = (byte)(i+1);
				
			}
		}
		return MTs;
	}
	
	
	
	public static List<byte[]> splitFrame(byte[] frame, int chunkSize, int firstPadSize, int nextPadSize) {
		
			
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
        	//if (logger.isInfoEnabled()) logger.debug("Current Chunk Size : " + currentChunkSize);
        	System.arraycopy(frame, begin, currentChunk, padSize, toCopy);
        	byteArrays.add(currentChunk);
        	
        	left = left - toCopy;
        	begin = begin + toCopy;
        }
        
        
        return byteArrays;
    }
	
	
}
