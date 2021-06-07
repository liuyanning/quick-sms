package com.drondea.sms.session.smgp;

import com.drondea.sms.channel.IChannelSessionCounters;
import com.drondea.sms.conf.ClientSocketConfig;
import com.drondea.sms.connector.IConnector;
import com.drondea.sms.connector.smgp.SmgpClientConnector;
import com.drondea.sms.session.AbstractClientSession;
import com.drondea.sms.session.AbstractClientSessionManager;
import com.drondea.sms.session.smgp.SmgpClientSession;
import com.drondea.sms.session.smgp.SmgpSessionCounters;
import com.drondea.sms.type.ICustomHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * @version V3.0
 * @description: smgp协议的session管理
 * @author: ywj
 * @date: 2020年06月08日09:40
 **/
public class SmgpClientSessionManager extends AbstractClientSessionManager {

    /**
     * 用户定制配置
     */
    private ICustomHandler customInterface;

    public SmgpClientSessionManager(ClientSocketConfig socketConfig, ICustomHandler customInterface) {
        super(socketConfig, customInterface);
        this.customInterface = customInterface;
    }

    @Override
    public IConnector getConnector() {
        return new SmgpClientConnector(this);
    }

    @Override
    public AbstractClientSession createSession(ChannelHandlerContext ctx) {
        return new SmgpClientSession(ctx, this);
    }

    @Override
    public ICustomHandler getCustomHandler() {
        return this.customInterface;
    }

    @Override
    public IChannelSessionCounters createSessionCounters() {
        return new SmgpSessionCounters();
    }
}
