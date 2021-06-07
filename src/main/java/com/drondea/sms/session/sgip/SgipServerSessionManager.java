package com.drondea.sms.session.sgip;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.channel.IChannelSessionCounters;
import com.drondea.sms.conf.ServerSocketConfig;
import com.drondea.sms.connector.IConnector;
import com.drondea.sms.connector.sgip.SgipServerConnector;
import com.drondea.sms.session.AbstractServerSessionManager;
import com.drondea.sms.session.sgip.SgipServerSession;
import com.drondea.sms.session.sgip.SgipSessionCounters;
import com.drondea.sms.type.ICustomHandler;
import com.drondea.sms.type.IValidator;
import com.drondea.sms.type.UserChannelConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * @version V3.0.0
 * @description: 服务器端session管理器
 * @author: liyuehai
 * @date: 2020年06月10日17:13
 **/
public class SgipServerSessionManager extends AbstractServerSessionManager {

    /**
     * 用户信息获取接口
     */
    private IValidator userInfoAccessor;

    public SgipServerSessionManager(IValidator userInfoAccessor,
                                    ServerSocketConfig socketConfig) {
        super(socketConfig, null);
        this.userInfoAccessor = userInfoAccessor;
    }

    public SgipServerSessionManager(IValidator userInfoAccessor,
                                    ServerSocketConfig socketConfig, ICustomHandler customHandler) {
        super(socketConfig, customHandler);
        this.userInfoAccessor = userInfoAccessor;
    }

    @Override
    public ChannelSession createSession(ChannelHandlerContext ctx) {
        return new SgipServerSession(ctx, this);
    }

    @Override
    public IChannelSessionCounters createSessionCounters() {
        return new SgipSessionCounters();
    }

    @Override
    public IConnector getConnector() {
        return new SgipServerConnector(this);
    }

    //todo 放宽一点，直接validtor,包括ip的校验等等
    @Override
    public UserChannelConfig getUserChannelConfig(String userName) {
        return userInfoAccessor.getUserChannelConfig(userName);
    }
}
