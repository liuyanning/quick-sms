package com.drondea.sms.session.cmpp;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.channel.IChannelSessionCounters;
import com.drondea.sms.conf.ServerSocketConfig;
import com.drondea.sms.connector.cmpp.CmppServerConnector;
import com.drondea.sms.connector.IConnector;
import com.drondea.sms.session.AbstractServerSessionManager;
import com.drondea.sms.session.cmpp.CmppServerSession;
import com.drondea.sms.session.cmpp.CmppSessionCounters;
import com.drondea.sms.type.ICustomHandler;
import com.drondea.sms.type.UserChannelConfig;
import com.drondea.sms.type.IValidator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * @version V3.0.0
 * @description: 服务器端session管理器
 * @author: 刘彦宁
 * @date: 2020年06月10日17:13
 **/
public class CmppServerSessionManager extends AbstractServerSessionManager {

    /**
     * 用户信息获取接口
     */
    private IValidator userInfoAccessor;

    public CmppServerSessionManager(IValidator userInfoAccessor,
                                    ServerSocketConfig socketConfig) {
        super(socketConfig, null);
        this.userInfoAccessor = userInfoAccessor;
    }

    public CmppServerSessionManager(IValidator userInfoAccessor,
                                    ServerSocketConfig socketConfig, ICustomHandler customHandler) {
        super(socketConfig, customHandler);
        this.userInfoAccessor = userInfoAccessor;
    }

    @Override
    public ChannelSession createSession(ChannelHandlerContext ctx) {
        return new CmppServerSession(ctx, this);
    }

    @Override
    public IChannelSessionCounters createSessionCounters() {
        return new CmppSessionCounters();
    }

    @Override
    public IConnector getConnector() {
        return new CmppServerConnector(this);
    }

    //todo 放宽一点，直接validtor,包括ip的校验等等
    @Override
    public UserChannelConfig getUserChannelConfig(String userName) {
        return userInfoAccessor.getUserChannelConfig(userName);
    }
}
