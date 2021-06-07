package com.drondea.sms.session.sgip;

import com.drondea.sms.channel.IChannelSessionCounters;
import com.drondea.sms.conf.ClientSocketConfig;
import com.drondea.sms.connector.IConnector;
import com.drondea.sms.connector.sgip.SgipClientConnector;
import com.drondea.sms.session.AbstractClientSession;
import com.drondea.sms.session.AbstractClientSessionManager;
import com.drondea.sms.session.sgip.SgipClientSession;
import com.drondea.sms.session.sgip.SgipSessionCounters;
import com.drondea.sms.type.ICustomHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * @version V3.0.0
 * @description: Sgip协议的session管理
 * @author: liyuehai
 * @date: 2020年06月08日09:40
 **/
public class SgipClientSessionManager extends AbstractClientSessionManager {

    public SgipClientSessionManager(ClientSocketConfig socketConfig, ICustomHandler customInterface) {
        super(socketConfig, customInterface);
    }

    @Override
    public IConnector getConnector() {
        return new SgipClientConnector(this);
    }

    @Override
    public AbstractClientSession createSession(ChannelHandlerContext ctx) {
        return new SgipClientSession(ctx, this);
    }

    @Override
    public IChannelSessionCounters createSessionCounters() {
        return new SgipSessionCounters();
    }

}
