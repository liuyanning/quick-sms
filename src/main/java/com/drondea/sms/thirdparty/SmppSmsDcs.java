package com.drondea.sms.thirdparty;

public class SmppSmsDcs extends AbstractSmsDcs {
    private static final long serialVersionUID = 1L;

    public SmppSmsDcs(byte dcs) {
        super(dcs);
    }

    /**
     * Builds a general-data-coding dcs.
     *
     * @param alphabet The alphabet.
     * @return A valid general data coding DCS.
     */
    public static SmppSmsDcs getGeneralDataCodingDcs(SmsAlphabet alphabet) {
        byte dcs = 0x00;

        switch (alphabet) {
            case GSM:
                dcs |= 0x00;
                break;
            case ASCII:
                dcs |= 0x01;
                break;
            case LATIN1:
                dcs |= 0x03;
                break;
            case UCS2:
                dcs |= 0x08;
                break;
            case RESERVED:
                dcs |= 0x0F;
                break;
            default:
                dcs = 0x00;
        }

        return new SmppSmsDcs(dcs);
    }


    /**
     * Decodes the given dcs and returns the alphabet.
     *
     * @return Returns the alphabet or NULL if it was not possible to find an
     * alphabet for this dcs.
     */
    @Override
    public SmsAlphabet getAlphabet() {
//		switch (getGroup()) {
//		case GENERAL_DATA_CODING:
//		case MSG_MARK_AUTO_DELETE:
//		case MESSAGE_WAITING_DISCARD:
        // General Data Coding Indication

        switch (dcs_) {
            case 0:
                return SmsAlphabet.GSM;
            case 1:
                return SmsAlphabet.ASCII;
            case 3:
                return SmsAlphabet.LATIN1;
            case 4:
                return SmsAlphabet.LATIN1;
            case 8:
                return SmsAlphabet.UCS2;
            case 15:
                return SmsAlphabet.RESERVED;
        }

        if (dcs_ == 0x00) {
            return SmsAlphabet.GSM;
        }

        switch (dcs_ & 0x0C) {
            case 0x00:
                return SmsAlphabet.GSM;
            case 0x04:
                return SmsAlphabet.LATIN1;
            case 0x08:
                return SmsAlphabet.UCS2;
            case 0x0C:
                return SmsAlphabet.RESERVED;
            default:
                return SmsAlphabet.UCS2;
        }

//		case MESSAGE_WAITING_STORE_GSM:
//			return SmsAlphabet.GSM;
//
//		case MESSAGE_WAITING_STORE_UCS2:
//			return SmsAlphabet.UCS2;
//
//		case DATA_CODING_MESSAGE:
//			switch (dcs_ & 0x04) {
//			case 0x00:
//				return SmsAlphabet.GSM;
//			case 0x04:
//				return SmsAlphabet.LATIN1;
//			default:
//				return SmsAlphabet.UCS2;
//			}
//
//		default:
//			return SmsAlphabet.UCS2;
//		}
    }

    @Override
    public int getMaxMsglength() {
        //GSM是7字节编码
        switch (getAlphabet()) {
            case GSM:
                return 160;
            default:
                return 140;
        }
    }

}
