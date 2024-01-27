package com.mobiera.java.sim.tools;
/*See 


https://docs.google.com/document/d/1NNmFlVO1kUoj4Db14HzYocR29Xw_oI9fcn5dWgEmN6U/edit#

	
	*/
public enum CharsetTechnology {
	// STK : GSM(8bits), UCS2
	// SMSC : PACKED_GSM(7bits), LATIN1, UCS2
	// SMSC_NO_PACKED_GSM : GSM(8bits), LATIN1, UCS2
	// SMSC_PACKED_GSM_UCS2_ONLY : GSM(7bits), LATIN1, UCS2
	// SMSC_GSM_UCS2_ONLY : GSM(8bits), UCS2
	
	STK,
	STK_NO_PACKED_GSM,
	STK_GSM_ONLY,
	STK_UCS2_ONLY,
	SMSC,
	SMSC_NO_PACKED_GSM,
	SMSC_GSM_UCS2_ONLY,
	SMSC_PACKED_GSM_UCS2_ONLY,
	SMSC_PACKED_GSM_ONLY,
	SMSC_GSM_ONLY,
	SMSC_NO_UCS2
}


/*

CharsetTechnology.SMSC : allow usage of all supported SMPP compatibles charsets, including  packed GSM, GSM, LATIN1 and UCS2 (default).
CharsetTechnology.SMSC_NO_PACKED_GSM : same as SMSC but with no packed GSM..
CharsetTechnology.SMSC_GSM_UCS2_ONLY : allow usage of GSM and UCS2 charset only.
CharsetTechnology.SMSC_PACKED_GSM_UCS2_ONLY : allow usage of packed GSM and UCS2 charsets only.
CharsetTechnology.SMSC_PACKED_GSM_ONLY : allow usage of packed GSM only.
CharsetTechnology.SMSC_GSM_ONLY  : allow usage of GSM only.
CharsetTechnology.SMSC_NO_UCS2 : allow usage of all supported SMPP compatibles charsets, including  packed GSM, GSM, LATIN1 less UCS2

*/