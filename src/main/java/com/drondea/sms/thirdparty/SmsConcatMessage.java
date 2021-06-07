/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is "SMS Library for the Java platform".
 *
 * The Initial Developer of the Original Code is Markus Eriksson.
 * Portions created by the Initial Developer are Copyright (C) 2002
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */
package com.drondea.sms.thirdparty;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Baseclass for messages that needs to be concatenated.
 * <p>- Only usable for messages that uses the same UDH fields for all message
 * parts. <br>- This class could be better written. There are several parts
 * that are copy- pasted. <br>- The septet coding could be a bit optimized.
 * <br>
 *
 * @author Markus Eriksson
 * @version $Id$
 */
public abstract class SmsConcatMessage implements SmsMessage {
    private static final AtomicInteger rnd_ = new AtomicInteger((new Random()).nextInt(0xffff));

    /**
     * Creates an empty SmsConcatMessage.
     */
    protected SmsConcatMessage() {
        // Empty
    }

    /**
     * Returns the whole UD
     *
     * @return the UD
     */
    public abstract SmsUserData getUserData();

    /**
     * Returns the udh elements
     * <p>
     * The returned UDH is the same as specified when the message was created.
     * No concat headers are added.
     *
     * @return the UDH as SmsUdhElements
     */
    public abstract SmsUdhElement[] getUdhElements();

    private int nextRandom() {
        return rnd_.incrementAndGet() & 0xff;
    }

    private SmsPdu[] createOctalPdus(SmsUdhElement[] udhElements, SmsUserData ud, int maxBytes) {
        int nMaxChars;
        int nMaxConcatChars;
        SmsPdu[] smsPdus = null;

        // 8-bit concat header is 6 bytes...
        //短信内容最多字节计算
        nMaxConcatChars = maxBytes - 6;
        //nMaxChars 是对应格式最长字节数，一般是140
        nMaxChars = maxBytes;

        //短信长度小于140，那就不拆分了，就一条短信
        if (ud.getLength() <= nMaxChars) {
            //这里udhElements是null，没有UDH
            smsPdus = new SmsPdu[]{new SmsPdu(udhElements, ud)};
        } else {


            // Calculate number of SMS needed
            int nSms = ud.getLength() / nMaxConcatChars;
            if ((ud.getLength() % nMaxConcatChars) > 0) {
                nSms += 1;
            }
            smsPdus = new SmsPdu[nSms];

            // Calculate number of UDHI
            SmsUdhElement[] pduUdhElements = null;
            if (udhElements == null) {
                pduUdhElements = new SmsUdhElement[1];
            } else {
                //针对nMaxConcatChars，多个头部信息只算了一个，会不会有问题？现在暂时用不到这里
                pduUdhElements = new SmsUdhElement[udhElements.length + 1];
                // Copy the UDH headers
                System.arraycopy(udhElements, 0, pduUdhElements, 1, udhElements.length);
            }

            //服务器端接收到再加上电话号码和spId判断重复，同一号码同一spid发送长短信会导致问题
            //长短信批量号
            int refno = nextRandom();

            // Create pdus
            for (int i = 0; i < nSms; i++) {
                byte[] pduUd;
                int udBytes;
                int udLength;
                int udOffset;

                // Create concat header
                pduUdhElements[0] = SmsUdhUtil.get8BitConcatUdh(refno, nSms, i + 1);

                // Create
                // Must concatenate messages
                // Calc pdu length
                udOffset = nMaxConcatChars * i;
                //减去一条长度后，剩余长度
                udBytes = ud.getLength() - udOffset;
                //剩余长度大于一条短信长度，拆分一条短信
                if (udBytes > nMaxConcatChars) {
                    udBytes = nMaxConcatChars;
                }
                //拆分短信长度
                udLength = udBytes;

                //创建短信内容字节数组，并进行赋值
                pduUd = new byte[udBytes];
                System.arraycopy(ud.getData(), udOffset, pduUd, 0, udBytes);
                smsPdus[i] = new SmsPdu(pduUdhElements, pduUd, udLength, ud.getDcs());
            }
        }
        return smsPdus;
    }

    private SmsPdu[] createUnicodePdus(SmsUdhElement[] udhElements, SmsUserData ud, int maxBytes) {

        if (ud.getLength() <= maxBytes) {
            return new SmsPdu[]{new SmsPdu(udhElements, ud)};
        }

        int refno = nextRandom();
        // 8-bit concat header is 6 bytes...
        int nMaxConcatChars = (maxBytes - 6) / 2;
        int realCharLength = ud.getLength() / 2;
        // Calculate number of SMS needed
        int nSms = realCharLength / nMaxConcatChars;
        if (realCharLength % nMaxConcatChars > 0) {
            nSms += 1;
        }
        SmsPdu[] smsPdus = new SmsPdu[nSms];

        // Calculate number of UDHI
        SmsUdhElement[] pduUdhElements;
        if (udhElements == null) {
            pduUdhElements = new SmsUdhElement[1];
        } else {
            pduUdhElements = new SmsUdhElement[udhElements.length + 1];

            // Copy the UDH headers
            System.arraycopy(udhElements, 0, pduUdhElements, 1, udhElements.length);
        }

        // Create pdus
        for (int i = 0; i < nSms; i++) {
            byte[] pduUd;
            int udBytes;
            int udLength;
            int udOffset;

            // Create concat header
            pduUdhElements[0] = SmsUdhUtil.get8BitConcatUdh(refno, nSms, i + 1);

            // Create
            // Must concatenate messages
            // Calc pdu length
            udOffset = nMaxConcatChars * i;
            udLength = realCharLength - udOffset;
            if (udLength > nMaxConcatChars) {
                udLength = nMaxConcatChars;
            }
            udBytes = udLength * 2;

            pduUd = new byte[udBytes];
            System.arraycopy(ud.getData(), udOffset * 2, pduUd, 0, udBytes);
            smsPdus[i] = new SmsPdu(pduUdhElements, pduUd, udBytes, ud.getDcs());
        }
        return smsPdus;
    }

    /**
     * 7字节数据的处理
     *
     * @param udhElements
     * @param ud
     * @param maxBytes
     * @return
     */
    private SmsPdu[] createSeptetPdus(SmsUdhElement[] udhElements, SmsUserData ud, int maxBytes) {
        int nMaxChars;
        int nMaxConcatChars;
        SmsPdu[] smsPdus = null;

        // 8-bit concat header is 6 bytes...
        nMaxConcatChars = ((maxBytes - 6) * 8) / 7;
        nMaxChars = (maxBytes * 8) / 7;

        if (ud.getLength() <= nMaxChars) {
            smsPdus = new SmsPdu[]{new SmsPdu(udhElements, ud)};
        } else {
            int refno = nextRandom();
            // Convert septets into a string...
            String msg = SmsPduUtil.readSeptets(ud.getData(), ud.getLength());

            if (msg.length() <= ud.getLength()) {
                //原字符串长度小于udLength ，说明存在GSM的转义字符
                //计算转义字符个数,拆分后长度要减去转义字符
                int cnt = SmsPduUtil.countGSMescapechar(msg);
                nMaxConcatChars -= 2 * cnt;
            }
            // Calculate number of SMS needed
            int nSms = msg.length() / nMaxConcatChars;
            if ((msg.length() % nMaxConcatChars) > 0) {
                nSms += 1;
            }
            smsPdus = new SmsPdu[nSms];

            // Calculate number of UDHI
            SmsUdhElement[] pduUdhElements = null;
            if (udhElements == null) {
                pduUdhElements = new SmsUdhElement[1];
            } else {
                pduUdhElements = new SmsUdhElement[udhElements.length + 1];

                // Copy the UDH headers
                System.arraycopy(udhElements, 0, pduUdhElements, 1, udhElements.length);
            }


            // Create pdus
            for (int i = 0; i < nSms; i++) {
                byte[] pduUd;
                int udOffset;
                int udLength;

                // Create concat header
                pduUdhElements[0] = SmsUdhUtil.get8BitConcatUdh(refno, nSms, i + 1);

                // Create
                // Must concatenate messages
                // Calc pdu length
                udOffset = nMaxConcatChars * i;
                udLength = ud.getLength() - udOffset;
                if (udLength > nMaxConcatChars) {
                    udLength = nMaxConcatChars;
                }

                if (udOffset + udLength > msg.length()) {
                    //转成7字节的
                    pduUd = SmsPduUtil.getSeptets(msg.substring(udOffset));
                } else {
                    //转成7字节的
                    pduUd = SmsPduUtil.getSeptets(msg.substring(udOffset, udOffset + udLength));
                }

                smsPdus[i] = new SmsPdu(pduUdhElements, pduUd, udLength, ud.getDcs());
            }
        }
        return smsPdus;
    }

    /**
     * 7字节数据的处理,压缩数据算法
     *
     * @param udhElements
     * @param ud
     * @param maxBytes
     * @return
     */
    private SmsPdu[] createSeptetPdusPack(SmsUdhElement[] udhElements, SmsUserData ud, int maxBytes) {
        int nMaxChars;
        int nMaxConcatChars;
        SmsPdu[] smsPdus = null;

        // 8-bit concat header is 6 bytes...
        nMaxConcatChars = 140 - 7;
        //最多140个字节包含160个字符
        nMaxChars = 140;

        //短信长度小于140，那就不拆分了，就一条短信
        if (ud.getLength() <= nMaxChars) {
            //这里udhElements是null，没有UDH
            smsPdus = new SmsPdu[]{new SmsPdu(udhElements, ud)};
        } else {


            // Calculate number of SMS needed
            int nSms = ud.getLength() / nMaxConcatChars;
            if ((ud.getLength() % nMaxConcatChars) > 0) {
                nSms += 1;
            }
            smsPdus = new SmsPdu[nSms];

            // Calculate number of UDHI
            SmsUdhElement[] pduUdhElements  = new SmsUdhElement[1];

            //服务器端接收到再加上电话号码和spId判断重复，同一号码同一spid发送长短信会导致问题
            //长短信批量号
            int refno = nextRandom();

            // Create pdus
            for (int i = 0; i < nSms; i++) {
                byte[] pduUd;
                int udBytes;
                int udLength;
                int udOffset;

                // Create concat header,这里使用7字节的udh
                pduUdhElements[0] = SmsUdhUtil.get16BitConcatUdh(refno, nSms, i + 1);

                // Create
                // Must concatenate messages
                // Calc pdu length
                udOffset = nMaxConcatChars * i;
                //减去一条长度后，剩余长度
                udBytes = ud.getLength() - udOffset;
                //剩余长度大于一条短信长度，拆分一条短信
                if (udBytes > nMaxConcatChars) {
                    udBytes = nMaxConcatChars;
                }
                //拆分短信长度
                udLength = udBytes;

                //创建短信内容字节数组，并进行赋值
                pduUd = new byte[udLength];
                //数据部分第一个字节填充0
                System.arraycopy(ud.getData(), udOffset, pduUd, 0, udBytes);
                smsPdus[i] = new SmsPdu(pduUdhElements, pduUd, udLength, ud.getDcs());
            }
        }
        return smsPdus;
    }


    /**
     * Converts this message into SmsPdu:s
     * <p>
     * If the message is too long to fit in one SmsPdu the message is divided
     * into many SmsPdu:s with a 8-bit concat pdu UDH element.
     *
     * @return Returns the message as SmsPdu:s
     */
    @Override
    public SmsPdu[] getPdus() {
        SmsPdu[] smsPdus;
        //根据编码格式将文本转换的字节数组
        SmsUserData ud = getUserData();
        //编码格式
        AbstractSmsDcs dcs = ud.getDcs();
        //获取User Data Header 元素，文本短信返回null
        SmsUdhElement[] udhElements = getUdhElements();
        //计算元素的个数，文本短信是0
        int udhLength = SmsUdhUtil.getTotalSize(udhElements);
        //计算一条短信除去User Data Header的长度，文本短信，这里8字节的情况下是140
        int nBytesLeft = dcs.getMaxMsglength() - udhLength;

        //根据不同的编码，对短信拆解成pdu，GSM是7字节，UCS2是16字节
        switch (dcs.getAlphabet()) {
            case GBK:
            case UCS2:
                smsPdus = createUnicodePdus(udhElements, ud, nBytesLeft);
                break;
            case GSM:
                smsPdus = createOctalPdus(udhElements, ud, nBytesLeft);
                break;
            case ASCII:
            case LATIN1:
            default:
                smsPdus = createOctalPdus(udhElements, ud, nBytesLeft);
                break;
        }

        return smsPdus;
    }
}
