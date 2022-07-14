package com.drondea.sms.handler.smgp;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.common.SequenceNumber;
import com.drondea.sms.common.util.CommonUtil;
import com.drondea.sms.message.IMessage;
import com.drondea.sms.message.smgp30.msg.SmgpSubmitRequestMessage;
import com.drondea.sms.thirdparty.SmsAlphabet;
import com.drondea.sms.thirdparty.SmsDcs;
import com.drondea.sms.type.ICustomHandler;
import com.drondea.sms.type.IMessageResponseHandler;
import com.drondea.sms.type.UserChannelConfig;
import com.drondea.sms.windowing.DuplicateKeyException;
import com.drondea.sms.windowing.OfferTimeoutException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.EventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @version V3.0
 * @description: smgp的定制处理器
 * @author: ywj
 * @date: 2020年06月23日17:35
 **/
public class SmgpClientCustomHandler extends ICustomHandler {

    private static final Logger logger = LoggerFactory.getLogger(SmgpClientCustomHandler.class);

    @Override
    public void fireUserLogin(Channel channel, ChannelSession channelSession) {

        //注册session可写状态监听器
        channelSession.setSessionEventHandler((writable) -> {
            System.out.println("可写状态发生改变：" + writable);
        });

        final EventExecutor executor = channel.pipeline().firstContext().executor();


        System.out.println("smgp用户登录成功--开始发送短信");


            int i = 0;
            while (true) {

                if (!channelSession.isWritable()) {
                                        System.out.println("不可写");
                    //                    continue;
                }
//                if (i == 0) {
//                    System.out.println("不发送短信 直接 break  ");
//                    break;
//                }
                SmgpSubmitRequestMessage requestMessage = new SmgpSubmitRequestMessage();
                SequenceNumber sequenceNumber = channelSession.getSequenceNumber();
                requestMessage.getHeader().setSequenceId(sequenceNumber.next());
                    String message = "您好您的验证码是：长短信您好您的验证码是：" + Math.random() + "【庄点科技】";
//                String message = "第条消息哈哈哈哈2" + Math.random();
                requestMessage.setMsgContent(message);
                requestMessage.setServiceId("1");
                requestMessage.setMsgSrc("AAAA");
                requestMessage.setSrcTermId("" + (int) (Math.random() * 1000));
                requestMessage.setNeedReport(true);
                requestMessage.setDestTermIdArray(new String[]{"17303110626"});
                requestMessage.setReserve("1234567");
                //收到响应的回调
                requestMessage.setMessageResponseHandler(new IMessageResponseHandler() {
                    @Override
                    public void messageComplete(IMessage request, IMessage response) {
//                            System.out.println(" 客户端提交短信 messageComplete ");
                    }

                    @Override
                    public void messageExpired(String key, IMessage request) {
                        System.out.println(" 客户端提交短信 messageExpired ");
                    }

                    @Override
                    public void sendMessageFailed(IMessage request) {

                    }
                });
                requestMessage.setMsgFmt(SmsDcs.getGeneralDataCodingDcs(SmsAlphabet.UCS2));
    //                if(i % 64 == 0) {
    //                    try {
    //                        Thread.sleep(2, 0);
    //                    } catch (InterruptedException e) {
    //                        e.printStackTrace();
    //                    }
    //                }
                //切分长短信
                List<IMessage> longMsgSlices = CommonUtil.getLongMsgSlices(requestMessage, channelSession.getConfiguration(), sequenceNumber);
                longMsgSlices.forEach(msg -> {
                    channelSession.sendMessage(msg);
                });

                i++;
                if (i == 6) {
                    System.out.println("只发送一条短信  break  ");
                    break;
                }
            }
    }

    @Override
    public void channelClosed(ChannelSession channelSession) {

    }

    @Override
    public void configPipelineAfterLogin(ChannelPipeline pipeline) {
        pipeline.addLast("SmgpTestSubmitResponseHandler", new SmgpTestSubmitResponseHandler());
        pipeline.addLast("SmgpTestDeliverResponseHandler", new SmgpTestDeliverRequestHandler());
    }

    @Override
    public void responseMessageExpired(Integer sequenceId, IMessage request) {
        System.out.println("短信超时处理" + sequenceId);
    }

    @Override
    public void slidingWindowException(ChannelSession session, ChannelHandlerContext ctx, IMessage message, ChannelPromise promise, Exception exception) {
        logger.error("slidingWindowException", exception);
        int retryCount = message.addRetryCount();
        //失败越多延时越长，防止线程堆积
        int delay = 10;
        if (retryCount >= 30) {
            delay = 500 * retryCount / 10;
        }
        if (retryCount > 20) {
            System.out.println("重试" + retryCount);
        }
        //重写
        ctx.executor().schedule(() -> {
            session.sendWindowMessage(ctx, message, promise);
        }, delay, TimeUnit.MILLISECONDS);
        //滑动窗口key冲突
        if (exception instanceof DuplicateKeyException) {
            return;
        }
        //滑动窗口获取slot超时
        if (exception instanceof OfferTimeoutException) {
            return;
        }
        if (exception instanceof InterruptedException) {
            return;
        }
    }

    @Override
    public boolean customLoginValidate(IMessage message, UserChannelConfig channelConfig, Channel channel) {
        System.out.println(" TODO SMGP 自定义登录校验，直接返回 true");
        return true;
    }

}
