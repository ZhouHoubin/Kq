package z.houbin.kq.kg;

import org.json.JSONObject;
import z.houbin.kq.http.HttpResponse;
import z.houbin.kq.http.HttpUtils;
import z.houbin.kq.http.KqWebSocketClient;
import z.houbin.kq.http.MessageListener;
import z.houbin.kq.util.TextUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KgTask {
    private static KqWebSocketClient webSocketClient;

    public void start() {
        try {
            webSocketClient = new KqWebSocketClient(new URI("ws://localhost:25303"));
            webSocketClient.setMessageListener(kgMessageListener);
            webSocketClient.connect();
            System.out.println("运行中....");
        } catch (URISyntaxException e) {
            //e.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                super.run();
                if (webSocketClient != null && webSocketClient.isOpen()) {
                    webSocketClient.close();
                }
            }
        });
    }

    private static MessageListener kgMessageListener = new MessageListener() {
        @Override
        public void onAct_2(int type, JSONObject json) {
            //群消息
            try {
                String qq = json.getString("fromQQ");
                String group = json.getString("fromGroup");
                if (group.equals("297234598") && !qq.equals("2329264277")) {
                    System.out.println("聊天 : ");
                    String[] menus = new String[]{"菜单", "讲笑话", "鬼故事", "猜歌名", "竞猜", "猜成语", "成语接龙", "数箱子", "你画我猜", "明星"};
                    Random random = new Random();
                    String msg = String.format(Locale.CHINA, "[CQ:at,qq=%s] %s", "2854196306", menus[random.nextInt(menus.length - 1)] + " " + System.currentTimeMillis());
                    JSONObject msgObj = new JSONObject();
                    msgObj.put("act", "101");
                    msgObj.put("groupid", group);
                    msgObj.put("msg", msg);
                    //webSocketClient.send(msgObj);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAct_4(int type, JSONObject json) {
            //讨论组消息
        }

        @Override
        public void onAct_21(int type, JSONObject json) {
            //私聊消息
            String msg = json.getString("msg");
            String qq = json.getString("fromQQ");

//            JSONObject data = new JSONObject();
//            JSONObject inputText = new JSONObject();
//            inputText.put("text",msg);
//            JSONObject perception = new JSONObject();
//            perception.put("inputText",inputText);
//
//            JSONObject userInfo = new JSONObject();
//            userInfo.put("apiKey","be24ad78b46c5c9549acf1f6d580c3bb");
//            userInfo.put("userId",qq);
//            data.put("perception",perception);
//            data.put("userInfo",userInfo);
//
//            HttpUtils utils = new HttpUtils();
//            try {
//                HttpResponse response = utils.sendPost("http://openapi.tuling123.com/openapi/api/v2",data.toString(),null);
//                String result = response.content;
//                System.out.println(result);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            try {
                HttpUtils utils = new HttpUtils();
                String result = utils.sendGet("http://api.qingyunke.com/api.php?key=free&appid=0&msg="+msg).content;
                JSONObject resData = new JSONObject(result);
                if(resData.getInt("result")==0){
                    String text = resData.getString("content");
                    JSONObject msgObj = new JSONObject();
                    msgObj.put("act", "106");
                    msgObj.put("QQID", qq);
                    msgObj.put("msg", text.replaceAll("\\{br\\}","\r\n"));
                    webSocketClient.send(msgObj);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            if (group.equals("297234598")) {
                String welcomeMsg = String.format(Locale.CHINA, "[CQ:at,qq=%s] 欢迎欢迎!", qq);
                JSONObject welcomeMessage = new JSONObject();
                welcomeMessage.put("act", "101");
                welcomeMessage.put("groupid", group);
                welcomeMessage.put("msg", welcomeMsg);
                webSocketClient.send(welcomeMessage);

                //String weiboMsg = String.format(Locale.CHINA, "[CQ:at,qq=%s] http://weibo.com/u/5088015722 互粉 互赞 秒回!", qq);
                //JSONObject weiboMessage = new JSONObject();
                //weiboMessage.put("act", "101");
                //weiboMessage.put("groupid", group);
                //weiboMessage.put("msg", weiboMsg);
                //webSocketClient.send(weiboMessage);
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
    };
}
