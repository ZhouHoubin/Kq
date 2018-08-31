package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

public class Controller implements MessageListener {
    @FXML
    Button connect;
    private KqWebSocketClient webSocketClient;

    public void onConnectClick(ActionEvent event) {
        System.out.println("Controller.onConnectClick");
        try {
            webSocketClient = new KqWebSocketClient(new URI("ws://localhost:25303"));
            webSocketClient.setMessageListener(this);
            webSocketClient.connect();
            isConnect = true;
            connect.setText("运行中...");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            isConnect = false;
        }
    }

    public boolean isConnect;

    public Stage stage;

    public void closeSocket() {
        webSocketClient.close();
    }

    @Override
    public void onAct_2(int type, JSONObject json) {
        //群消息
    }

    @Override
    public void onAct_4(int type, JSONObject json) {
        //讨论组消息
    }

    @Override
    public void onAct_21(int type, JSONObject json) {
        //私聊消息
    }

    @Override
    public void onAct_101(int type, JSONObject json) {
        //群事件-管理员变动
    }

    @Override
    public void onAct_102(int type, JSONObject json) {
        //群事件-群成员减少
    }

    @Override
    public void onAct_103(int type, JSONObject json) {
        // 群事件-群成员增加
        System.out.println(json);
        System.out.println("入群===========================");
        String qq = json.getString("beingOperateQQ");
        String group = json.getString("fromGroup");
        if (group.equals("786122281")) {
            String welcomeMsg = String.format(Locale.CHINA, "[CQ:at,qq=%s] 欢迎欢迎!", qq);
            JSONObject welcomeMessage = new JSONObject();
            welcomeMessage.put("act", "101");
            welcomeMessage.put("groupid", group);
            welcomeMessage.put("msg", welcomeMsg);
            webSocketClient.send(welcomeMessage);

            String weiboMsg = String.format(Locale.CHINA, "[CQ:at,qq=%s] http://weibo.com/u/5088015722 互粉 互赞 秒回!", qq);
            JSONObject weiboMessage = new JSONObject();
            weiboMessage.put("act", "101");
            weiboMessage.put("groupid", group);
            weiboMessage.put("msg", weiboMsg);
            webSocketClient.send(weiboMessage);
        }
    }

    @Override
    public void onAct_201(int type, JSONObject json) {
        //好友事件-好友已添加
    }

    @Override
    public void onAct_301(int type, JSONObject json) {
        //请求-好友添加
    }

    @Override
    public void onAct_302(int type, JSONObject json) {
        //请求-群添加
    }
}
