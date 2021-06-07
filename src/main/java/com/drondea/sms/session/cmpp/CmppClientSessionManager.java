package com.drondea.sms.session.cmpp;

import com.drondea.sms.channel.IChannelSessionCounters;
import com.drondea.sms.connector.cmpp.CmppClientConnector;
import com.drondea.sms.connector.IConnector;
import com.drondea.sms.conf.ClientSocketConfig;
import com.drondea.sms.session.AbstractClientSession;
import com.drondea.sms.session.AbstractClientSessionManager;
import com.drondea.sms.session.cmpp.CmppClientSession;
import com.drondea.sms.session.cmpp.CmppSessionCounters;
import com.drondea.sms.type.ICustomHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * @version V3.0.0
 * @description: cmpp协议的session管理
 * @author: 刘彦宁
 * @date: 2020年06月08日09:40
 **/
public class CmppClientSessionManager extends AbstractClientSessionManager {

    public CmppClientSessionManager(ClientSocketConfig socketConfig, ICustomHandler customInterface) {
        super(socketConfig, customInterface);
    }

    @Override
    public IConnector getConnector() {
        return new CmppClientConnector(this);
    }

    @Override
    public AbstractClientSession createSession(ChannelHandlerContext ctx) {
        return new CmppClientSession(ctx, this);
    }

    @Override
    public IChannelSessionCounters createSessionCounters() {
        return new CmppSessionCounters();
    }

}
