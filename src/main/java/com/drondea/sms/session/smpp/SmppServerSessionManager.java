package com.drondea.sms.session.smpp;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.channel.IChannelSessionCounters;
import com.drondea.sms.conf.ServerSocketConfig;
import com.drondea.sms.connector.IConnector;
import com.drondea.sms.connector.smpp.SmppServerConnector;
import com.drondea.sms.session.AbstractServerSessionManager;
import com.drondea.sms.session.cmpp.CmppSessionCounters;
import com.drondea.sms.session.smpp.SmppServerSession;
import com.drondea.sms.type.ICustomHandler;
import com.drondea.sms.type.IValidator;
import com.drondea.sms.type.UserChannelConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * @version V3.0.0
 * @description: 服务器端session管理器
 * @author: gengjinbiao
 * @date: 2020年07月15日17:13
 **/
public class SmppServerSessionManager extends AbstractServerSessionManager {

    /**
     * 用户信息获取接口
     */
    private IValidator userInfoAccessor;

    public SmppServerSessionManager(IValidator userInfoAccessor,
                                    ServerSocketConfig socketConfig) {
        super(socketConfig, null);
        this.userInfoAccessor = userInfoAccessor;
    }

    public SmppServerSessionManager(IValidator userInfoAccessor,
                                    ServerSocketConfig socketConfig, ICustomHandler customHandler) {
        super(socketConfig, customHandler);
        this.userInfoAccessor = userInfoAccessor;
    }

    @Override
    public ChannelSession createSession(ChannelHandlerContext ctx) {
        return new SmppServerSession(ctx, this);
    }

    @Override
    public IChannelSessionCounters createSessionCounters() {
        return new SmppSessionCounters();
    }

    @Override
    public IConnector getConnector() {
        return new SmppServerConnector(this);
    }

    //todo 放宽一点，直接validtor,包括ip的校验等等
    @Override
    public UserChannelConfig getUserChannelConfig(String userName) {
        return userInfoAccessor.getUserChannelConfig(userName);
    }
}
