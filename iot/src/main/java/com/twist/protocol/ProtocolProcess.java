package com.twist.protocol;

import org.springframework.stereotype.Component;


/**
 * @description: 协议处理
 * @author: chenyingjie
 * @create: 2019-01-07 19:46
 **/
@Component
public class ProtocolProcess {

    private Connect connect;

    private Subscribe subscribe;

    private UnSubscribe unSubscribe;

    private Publish publish;

    private DisConnect disConnect;

    private PingReq pingReq;

    private PubRel pubRel;

    private PubAck pubAck;

    private PubRec pubRec;

    private PubComp pubComp;

}
