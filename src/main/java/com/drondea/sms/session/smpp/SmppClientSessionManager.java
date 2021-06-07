package com.drondea.sms.session.smpp;

import com.drondea.sms.channel.IChannelSessionCounters;
import com.drondea.sms.conf.ClientSocketConfig;
import com.drondea.sms.connector.IConnector;
import com.drondea.sms.connector.smpp.SmppClientConnector;
import com.drondea.sms.session.AbstractClientSession;
import com.drondea.sms.session.AbstractClientSessionManager;
import com.drondea.sms.session.smpp.SmppClientSession;
import com.drondea.sms.type.ICustomHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * @version V3.0.0
 * @description: smpp协议的session管理
 * @author: gengjinbiao
 * @date: 2020年07月10日09:40
 **/
public class SmppClientSessionManager extends AbstractClientSessionManager {

    /**
     * 用户定制配置
     */
    private ICustomHandler customInterface;

    public SmppClientSessionManager(ClientSocketConfig socketConfig, ICustomHandler customInterface) {
        super(socketConfig, customInterface);
        this.customInterface = customInterface;
    }

    @Override
    protected IConnector getConnector() {
        return new SmppClientConnector(this);
    }

    @Override
    public AbstractClientSession createSession(ChannelHandlerContext ctx) {
        return new SmppClientSession(ctx, this);
    }

    @Override
    public ICustomHandler getCustomHandler() {
        return this.customInterface;
    }

    @Override
    public IChannelSessionCounters createSessionCounters() {
        return new SmppSessionCounters();
    }
}
