package com.twist.mqtt.protocol;

import com.twist.mqtt.PubAck;
import com.twist.mqtt.PubComp;
import com.twist.mqtt.PubRec;
import com.twist.mqtt.PubRel;
import com.twist.mqtt.Publish;
import com.twist.mqtt.Subscribe;
import com.twist.mqtt.UnSubscribe;
import org.springframework.stereotype.Component;


/**
 * @description: 协议处理
 * @author: chenyingjie
 * @create: 2019-01-07 19:46
 **/
@Component
public class ProtocolProcess {

    private com.twist.mqtt.protocol.Connect connect;

    private Subscribe subscribe;

    private UnSubscribe unSubscribe;

    private Publish publish;

    private com.twist.mqtt.protocol.DisConnect disConnect;

    private com.twist.mqtt.protocol.PingReq pingReq;

    private PubRel pubRel;

    private PubAck pubAck;

    private PubRec pubRec;

    private PubComp pubComp;

}
