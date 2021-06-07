package com.drondea.sms.session.smgp;

import com.drondea.sms.channel.ChannelSession;
import com.drondea.sms.channel.IChannelSessionCounters;
import com.drondea.sms.conf.ServerSocketConfig;
import com.drondea.sms.connector.IConnector;
import com.drondea.sms.connector.smgp.SmgpServerConnector;
import com.drondea.sms.session.AbstractServerSessionManager;
import com.drondea.sms.session.smgp.SmgpServerSession;
import com.drondea.sms.session.smgp.SmgpSessionCounters;
import com.drondea.sms.type.ICustomHandler;
import com.drondea.sms.type.IValidator;
import com.drondea.sms.type.UserChannelConfig;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * @version V3.0
 * @description: smgp服务器端session管理器
 * @author: ywj
 * @date: 2020年06月10日17:13
 **/
public class SmgpServerSessionManager extends AbstractServerSessionManager {

    /**
     * 用户信息获取接口
     */
    private IValidator userInfoAccessor;

    public SmgpServerSessionManager(IValidator userInfoAccessor, ServerSocketConfig socketConfig) {
        super(socketConfig, null);
        this.userInfoAccessor = userInfoAccessor;
    }

    public SmgpServerSessionManager(IValidator userInfoAccessor,
                                    ServerSocketConfig socketConfig, ICustomHandler customHandler) {
        super(socketConfig, customHandler);
        this.userInfoAccessor = userInfoAccessor;
    }

    @Override
    public ChannelSession createSession(ChannelHandlerContext ctx) {
        return new SmgpServerSession(ctx, this);
    }

    @Override
    public IChannelSessionCounters createSessionCounters() {
        return new SmgpSessionCounters();
    }

    @Override
    public IConnector getConnector() {
        return new SmgpServerConnector(this);
    }

    //todo 放宽一点，直接validtor,包括ip的校验等等
    @Override
    public UserChannelConfig getUserChannelConfig(String userName) {
        return userInfoAccessor.getUserChannelConfig(userName);
    }
}
