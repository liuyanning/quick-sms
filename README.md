# 短信协议中间件

## 介绍
此项目是基于netty的短信协议处理中间件，支持CMPP/SMGP/SGIP/SMPP等协议，集成了流速控制，实现了短信滑动窗口机制。此中间件已经成功应用在日发送量上亿的短信平台中，并且经受住了考验。

## 项目说明
此项目为完全开源项目，您可以完全基于此开发您自己的商业项目。此项目仅仅是短信协议处理的中间件，如果您需要完整的短信解决方案，请参考我司开源的[短信平台单机版](https://gitee.com/zhuang-dian/sms-platform-stand-alone)
在了解此项目前，您需要熟悉相关的协议细节和短信发送流程。

## 初衷
github上已经有开源的短信中间件了，为什么还要再开发一套？因为我们发现好多新手包括有一定基础的开发者在内经常为短信提交速度上不去，连接莫名断开等问题困扰。
这个中间件就是要为开发者处理好这些坑点，屏蔽这些细节，让开发者更专注于业务本身。如果你不是netty老手，那选择我们的中间件吧！

## 项目特点
1. 为开发者屏蔽了底层协议开发的繁琐和难点，开发者不必花精力解决控速和滑动窗口等难点。
2. 基于事件注册模式，所有response处理只需要注册相关事件处理器即可，全异步处理。
3. 支持基于滑动窗口的控速
4. 支持长短信的自动和并和拆分
5. 支持上游固定签名通道
6. 自动的连接重试机制
7. 丰富的自定义业务实现接口
使用此项目将极大简化您的协议开发流程，减少项目风险。此项目已经在我司开源的[短信平台单机版](https://gitee.com/zhuang-dian/sms-platform-stand-alone)中应用，您可参考项目中的应用来实现自己的业务需求。

## 使用说明
此项目支持两种发送模式，一种是提供消息提供者实现，
1.  消息提供者模式（推荐）
由于上下游处理消息的能力不对等，短信项目中一般会将下游提交的短信放置于缓存和队列中，针对此特点我们设计出消息提供者模式，此模式只需要实现MessageProvider从缓存或者队列中获取要发送的消息即可，无需关心速度控制等细节，我司系统都是基于此模式实现。
2.  主动发送模式
您需要自己实现发送的流速控制，不推荐使用

## 滑动窗口原理
滑动窗口是为了控制在收到对方response之前，我方系统同时发送的数据包的数量，可类比TCP的滑动窗口原理，如下图所示：
![image](docs/images/slidingWindow.png)
1. 初始化一个大小为4的队列Queue
2. 短信发送后放入队列Queue中，如果队列内已经包含4个消息那么不允许再发送短信,上图中已经包含t1,t2,t3,t4四条短信，不允许再发送
3. 当收到对方的Response响应后释放对应Queue中对象，这样Queue中有空闲未知即可再次发送短信，上图中t1收到response后删除，窗口空余1个，可以发送t5
4. 针对长期没有Response的对象有单独的线程定时扫描Queue，并对超时的对象做超时处理，并释放

## 代码示例

#### CMPP服务器端监听
此程序用于监听下游发送的短信请求，并向下游发送回执和上行，完整程序请参考com.drondea.sms.cmpp.CmppServerPullModeTest类
```java
        //监听端口
        CmppServerSocketConfig socketConfig = new CmppServerSocketConfig("test", 7891);
        //自定义实现的短信接受处理程序（会添加handler处理下游短信请求）
        CmppServerCustomHandler customHandler = new CmppServerCustomHandler();
        //服务器端默认版本号2.0
        socketConfig.setVersion(CmppConstants.VERSION_20);
        CmppServerSessionManager sessionManager = new CmppServerSessionManager(name -> {
            //用户认证逻辑
            if (name.startsWith("100003")) {
                UserChannelConfig userChannelConfig = new UserChannelConfig();
                userChannelConfig.setUserName(name);
                userChannelConfig.setId(name);
                //设置滑动窗口数量
                userChannelConfig.setWindowSize(32);
                userChannelConfig.setPassword("123123");
                //设置用户限速
                userChannelConfig.setQpsLimit(5000);
                return userChannelConfig;
            }
            return null;
        }, socketConfig, customHandler);

        //设置消息提供者（从数据库或者缓存获取消息即可），这里获取的消息是回执或者上行短信
        sessionManager.setMessageProvider(new MessageProvider() {
            @Override
            public List<IMessage> getTcpMessages(ChannelSession channelSession) {

                int i = sum.incrementAndGet();
                if (i > 2) {
                    return null;
                }

                CmppDeliverRequestMessage mo = new CmppDeliverRequestMessage();
                SequenceNumber sequenceNumber = channelSession.getSequenceNumber();
                mo.getHeader().setSequenceId(sequenceNumber.next());
                mo.setRegisteredDelivery((short) 0);
                mo.setMsgContent("TEST");
                mo.setDestId("18010181663");
                //收到响应的回调
                mo.setMessageResponseHandler(new IMessageResponseHandler() {
                    @Override
                    public void messageComplete(IMessage request, IMessage response) {
                        System.out.println("完成:" + request.getSequenceId());
                    }

                    @Override
                    public void messageExpired(String key, IMessage request) {
                        System.out.println("短信超时======" + request.getSequenceId());
                    }

                    @Override
                    public void sendMessageFailed(IMessage request) {
                        System.out.println("send failure:" + request);
                    }
                });
                mo.setMsgFmt(SmsDcs.getGeneralDataCodingDcs(SmsAlphabet.RESERVED));
                return CommonUtil.getLongMsgSlices(mo, channelSession.getConfiguration(), sequenceNumber);
            }

            @Override
            public void responseMessageMatchFailed(String requestKey, IMessage response) {

            }
        });
        sessionManager.doOpen();

```
#### CMPP发送短信示例
此程序是向上游通道发送短信的入口程序，负责提交短信和接收上游的回执和上行短信，完整示例请参考com.drondea.sms.cmpp.CmppClientPullModeTest类
```java
        String host = "127.0.0.1";
        //滑动窗口建议值为16
        CmppClientSocketConfig socketConfig = new CmppClientSocketConfig("test",
                10 * 1000, 16, host, 7891);
        socketConfig.setChannelSize(1);
        socketConfig.setUserName("100506");
        socketConfig.setPassword("123123");
        socketConfig.setVersion(CmppConstants.VERSION_20);
        //限速 条/s
        socketConfig.setQpsLimit(100);
        //固定签名设置
//        socketConfig.setSignatureDirection(SignatureDirection.CHANNEL_FIXED);
//        socketConfig.setSignaturePosition(SignaturePosition.PREFIX);
//        socketConfig.setSmsSignature("【庄点科技】");
        //开启超时监控,设置监控间隔时间，这个值最好是RequestExpiryTimeout的1/2
        socketConfig.setWindowMonitorInterval(10 * 1000);
        //设置响应超时时间
        socketConfig.setRequestExpiryTimeout(20 * 1000);
        //注册通道登陆事件，添加回执和上行的处理器
        CmppClientCustomHandler cmppCustomHandler = new CmppClientCustomHandler();

        CmppClientSessionManager sessionManager = new CmppClientSessionManager(socketConfig, cmppCustomHandler);
        //注册消息提供者，一般从MQ、缓存、数据库中获取数据
        sessionManager.setMessageProvider(new MessageProvider() {
            @Override
            public List<IMessage> getTcpMessages(ChannelSession channelSession) {
                int i = sum.incrementAndGet();
                if (i > 5) {
                    return null;
                }
                CmppSubmitRequestMessage requestMessage = new CmppSubmitRequestMessage();
                SequenceNumber sequenceNumber = channelSession.getSequenceNumber();
                requestMessage.getHeader().setSequenceId(sequenceNumber.next());
                String message = i + Math.random() + "第二次李白字太白，号青莲居士，【庄点】";
                requestMessage.setMsgContent(message);
                requestMessage.setServiceId("1");
                requestMessage.setMsgSrc("AAAA");
                requestMessage.setSrcId("" + (int) (Math.random() * 1000));
                requestMessage.setRegisteredDelivery((short) 1);
                requestMessage.setDestUsrTl((short) 1);
                requestMessage.setDestTerminalId(new String[]{"17303110626"});
                requestMessage.setSignature("【庄点科技】");
                //收到响应的回调
                requestMessage.setMessageResponseHandler(new IMessageResponseHandler() {
                    @Override
                    public void messageComplete(IMessage request, IMessage response) {
                        System.out.println("收到response:" + request.getSequenceId());
                    }
                    @Override
                    public void messageExpired(String key,IMessage request) {
                        System.out.println("短信超时======" + request.getSequenceId());
                    }
                    @Override
                    public void sendMessageFailed(IMessage request) {
                        System.out.println("短信发送失败:" + request);
                    }
                });
                requestMessage.setMsgFmt(SmsDcs.getGeneralDataCodingDcs(SmsAlphabet.RESERVED));
                //长短信拆分
                List<IMessage> longMsgSlices = CommonUtil.getLongMsgSlices(requestMessage, channelSession.getConfiguration(), channelSession.getSequenceNumber());
                return longMsgSlices;
            }

            @Override
            public void responseMessageMatchFailed(String requestKey, IMessage response) {
                System.out.println("上游没有response的情况处理");
            }
        });

        //创建链接
        sessionManager.doOpen();
        //定时检测连接，断开自动重连
        sessionManager.doCheckSessions();
```
## 技术讨论

交流QQ群：
![image](docs/images/qq.png)

## 致谢
- [cloudhopper-smpp](https://github.com/fizzed/cloudhopper-smpp) smpp的协议处理很多都是参考了这个项目
- [SMSGate](https://github.com/Lihuanghe/SMSGate) 业务很多思路都借鉴了李黄河大佬的SMSGate，重点感谢
