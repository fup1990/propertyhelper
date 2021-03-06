package com.gome.fup.easyconfig.handler;

import com.gome.fup.easyconfig.common.Config;
import com.gome.fup.easyconfig.common.Request;
import com.gome.fup.easyconfig.common.Response;
import com.gome.fup.easyconfig.service.ConfigService;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

/**
 * Created by fupeng-ds on 2017/7/14.
 */
public class ConfigHandler extends SimpleChannelInboundHandler<Request> {

    private ConfigService configService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request request) throws Exception {
        List<Config> configs = configService.getPropertyByProjectIdAndGroupName(request.getProjectId(), request.getGroupName(), request.getKey());
        Response response = buildResponse(request.getProjectId(), request.getGroupName(), configs);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private Response buildResponse(String projectId, String groupName, List<Config> configs) {
        Response response = new Response();
        response.setProjectId(projectId);
        response.setGroupName(groupName);
        response.setConfigs(configs);
        return response;
    }


    public ConfigHandler(ConfigService configService) {
        this.configService = configService;
    }
}
