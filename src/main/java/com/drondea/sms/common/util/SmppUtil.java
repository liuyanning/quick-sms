package com.drondea.sms.common.util;

import com.drondea.sms.message.smpp34.Tlv;
import io.netty.buffer.ByteBuf;

import java.io.UnsupportedEncodingException;

public class SmppUtil {


    public static int getStringLengthPlusOne(String str) {
        if (str == null) {
            return 1;
        }
        return str.getBytes().length + 1;
    }


    /**
     * Read and create a new Address from a buffer.  Checks if there is
     * a minimum number of bytes readable from the buffer.
     * @param buffer
     * @return
     * @throws UnrecoverablePduEncodingException
     * @throws RecoverablePduEncodingException
     */
//    static public Address readAddress(ChannelBuffer buffer) throws UnrecoverablePduException, RecoverablePduException {
//        // an address is at least 3 bytes long (ton, npi, and null byte)
//        if (buffer.readableBytes() < 3) {
//            throw new NotEnoughDataInBufferException("Parsing address", buffer.readableBytes(), 3);
//        }
//        Address address = new Address();
//        address.read(buffer);
//        return address;
//    }

    /**
     * Writes an address to a buffer.  If the address is null, this method will
     * safely write out the SmppConstants.EMPTY_ADDRESS instance.
     * @param buffer
     * @param value
     * @throws UnrecoverablePduEncodingException
     * @throws RecoverablePduEncodingException
     */
//    static public void writeAddress(ByteBuf buffer, Address value) throws UnrecoverablePduException, RecoverablePduException {
//        if (value == null) {
//            SmppConstants.EMPTY_ADDRESS.write(buffer);
//        } else {
//            value.write(buffer);
//        }
//    }


    /**
     * 从缓冲区读取TLV。这个方法是贪婪的，将读取字节即使不能成功完成。
     * 假设这方法将只被调用，如果它提前知道所有字节会提前提供。
     *
     * @param buffer The buffer to read from
     * @return A new TLV instance
     * @throws Exception
     */
    public static Tlv readTlv(ByteBuf buffer) throws Exception {
        // a TLV is at least 4 bytes (tag+length)
        if (buffer.readableBytes() < 4) {
            throw new Exception("Parsing TLV tag and length,ByteBuf:" + buffer.readableBytes());
        }

        short tag = buffer.readShort();
        int length = buffer.readUnsignedShort();

        // check if we have enough data for the TLV
        if (buffer.readableBytes() < length) {
            throw new Exception("Parsing TLV value,ByteBuf:" + buffer.readableBytes());
        }

        byte[] value = new byte[length];
        buffer.readBytes(value);

        return new Tlv(tag, value);
    }

    public static void writeTlv(ByteBuf buffer, Tlv tlv) throws Exception {
        if (tlv == null) {
            return;
        }
        buffer.writeShort(tlv.getTag());
        buffer.writeShort(tlv.getLength());
        if (tlv.getValue() != null) {
            buffer.writeBytes(tlv.getValue());
        }
    }

    /**
     * 将c字串(以null结尾)写入缓冲区。如果字符串为空
     * 此方法将只将空字节(0x00)写入缓冲区
     *
     * @param buffer
     * @param value
     * @throws Exception
     */
    public static void writeNullTerminatedString(ByteBuf buffer, String value) throws Exception {
        if (value != null) {
            try {
                byte[] bytes = value.getBytes("ISO-8859-1");
                buffer.writeBytes(bytes);
            } catch (UnsupportedEncodingException e) {
                throw new Exception(e.getMessage(), e);
            }
        }
        // always write null byte
        buffer.writeByte((byte) 0x00);
    }

    /**
     * 从缓冲区中读取c字符串(null结束)。该方法将尝试找到空字节，
     * 并读取所有数据，直到和包括空字节。返回的字符串不包括空字节。
     * 将抛出一个异常，如果没有空字节被发现，并耗尽数据在要读取的缓冲区中。
     *
     * @param buffer
     * @return
     * @throws Exception
     */
    public static String readNullTerminatedString(ByteBuf buffer) throws Exception {
        // maximum possible length are the readable bytes in buffer
        int maxLength = buffer.readableBytes();

        // if there are no readable bytes, return null
        if (maxLength == 0) {
            return null;
        }

        // the reader index is defaulted to the readerIndex
        int offset = buffer.readerIndex();
        int zeroPos = 0;

        // search for NULL byte until we hit end or find it
        while ((zeroPos < maxLength) && (buffer.getByte(zeroPos + offset) != 0x00)) {
            zeroPos++;
        }

        if (zeroPos >= maxLength) {
            // a NULL byte was not found
            throw new Exception("Terminating null byte not found after searching [" + maxLength + "] bytes");
        }

        // at this point, we found a terminating zero
        String result = null;
        if (zeroPos > 0) {
            // read a new byte array
            byte[] bytes = new byte[zeroPos];
            buffer.readBytes(bytes);
            try {
                result = new String(bytes, "ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                throw e;
            }
        } else {
            result = "";
        }

        // at this point, we have just one more byte to skip over (the null byte)
        byte b = buffer.readByte();
        if (b != 0x00) {
            throw new Exception("Impossible error: last byte read SHOULD have been a null byte, but was [" + b + "]");
        }

        return result;
    }

    // testUtils
    public static void printPDU(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.writerIndex()];
        byteBuf.readBytes(bytes);
        String str = byteArrToHex(bytes);
        for (int i = 0; i < str.length(); i++) {
            if (i % 2 == 0) {
                System.out.print(" ");
            }
            if (i % 32 == 0) {
                System.out.println(" ");
            }
            System.out.print(str.charAt(i));
        }
        System.out.println("");

    }

    private static final char HexCharArr[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static final String HexStr = "0123456789abcdef";

    public static String byteArrToHex(byte[] btArr) {
        char strArr[] = new char[btArr.length * 2];
        int i = 0;
        for (byte bt : btArr) {
            strArr[i++] = HexCharArr[bt >>> 4 & 0xf];
            strArr[i++] = HexCharArr[bt & 0xf];
        }
        return new String(strArr);
    }

    public static byte[] hexToByteArr(String hexStr) {
        char[] charArr = hexStr.toCharArray();
        byte btArr[] = new byte[charArr.length / 2];
        int index = 0;
        for (int i = 0; i < charArr.length; i++) {
            int highBit = HexStr.indexOf(charArr[i]);
            int lowBit = HexStr.indexOf(charArr[++i]);
            btArr[index] = (byte) (highBit << 4 | lowBit);
            index++;
        }
        return btArr;
    }


}
