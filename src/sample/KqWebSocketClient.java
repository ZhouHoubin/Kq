package sample;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.nio.channels.NotYetConnectedException;

//https://cqp.cc/forum.php?mod=viewthread&tid=29722&highlight=lemo
public class KqWebSocketClient extends WebSocketClient {
    private MessageListener messageListener;
    private Controller controller;

    public KqWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("KqWebSocketClient.onOpen");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("KqWebSocketClient.onMessage " + message);
        try {
            JSONObject json = new JSONObject(message);

            int act = json.getInt("act");
            int subType = json.getInt("subType");

            if (messageListener == null) {
                return;
            }

            switch (act) {
                case 2://群消息
                    messageListener.onAct_2(subType, json);
                    break;
                case 4://讨论组消息
                    messageListener.onAct_4(subType, json);
                    break;
                case 21://私聊消息
                    messageListener.onAct_21(subType, json);
                    break;
                case 101://群事件 管理员变动
                    messageListener.onAct_101(subType, json);
                    break;
                case 102://群事件 群成员减少
                    messageListener.onAct_102(subType, json);
                    break;
                case 103://群事件 群成员增加
                    messageListener.onAct_103(subType, json);
                    break;
                case 201://好友事件 好友已添加
                    messageListener.onAct_201(subType, json);
                    break;
                case 301://请求 好友添加
                    messageListener.onAct_301(subType, json);
                    break;
                case 302://请求 群添加
                    messageListener.onAct_302(subType, json);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void send(JSONObject json) {
        try {
            send(json.toString());
        } catch (NotYetConnectedException e) {
            e.printStackTrace();
        }
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("KqWebSocketClient.onClose");
        if (controller != null) {
            if (controller.stage != null) {
                controller.stage.close();
            }
        }
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("KqWebSocketClient.onError");
    }
}
