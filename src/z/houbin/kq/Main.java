package z.houbin.kq;

import org.json.JSONObject;
import z.houbin.kq.http.KqWebSocketClient;
import z.houbin.kq.http.MessageListener;
import z.houbin.kq.util.Config;
import z.houbin.kq.util.FileUtil;
import z.houbin.kq.util.TextUtils;
import z.houbin.kq.weibo.Weibo;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static KqWebSocketClient webSocketClient;
    private static Weibo weibo;

    public static void main(String[] args) {
        String cookie = FileUtil.read(new File("cookie.txt"));
        System.out.println(cookie);
        Config.cookie = cookie;
        Config.uid = "5088015722";
        if (TextUtils.isEmpty(cookie)) {
            System.out.println("Cookie 错误");
        } else {
            try {
                webSocketClient = new KqWebSocketClient(new URI("ws://localhost:25303"));
                webSocketClient.setMessageListener(messageListener);
                webSocketClient.connect();
                System.out.println("运行中....");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            weibo = new Weibo();
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

    private static MessageListener messageListener = new MessageListener() {
        @Override
        public void onAct_2(int type, JSONObject json) {
            //群消息
            try {
                String qq = json.getString("fromQQ");
                String group = json.getString("fromGroup");
                String msg = json.getString("msg");
                if (group.equals("786122281") && !qq.equals("2329264277")) {
                    String url = getRegex(msg);
                    if (url != null) {
                        String uid = getRegex(url, "\\d+");
                        if (TextUtils.isEmpty(uid)) {
                            return;
                        }

                        List<JSONObject> followeds = weibo.getFolloweds();
                        for (JSONObject followed : followeds) {
                            long id = followed.getJSONObject("user").getLong("id");
                            if (Long.valueOf(uid) == id) {
                                //已经关注
                                System.out.println("已经关注 " + url);
                                return;
                            }
                        }

                        JSONObject follow = weibo.follow(uid);
                        System.out.println("群聊关注 : " + follow);
                        if (follow != null) {
                            String weiboMsg = String.format(Locale.CHINA, "[CQ:at,qq=%s] http://weibo.com/u/5088015722 已粉 请回 ", qq);
                            JSONObject weiboMessage = new JSONObject();
                            weiboMessage.put("act", "101");
                            weiboMessage.put("groupid", group);
                            weiboMessage.put("msg", weiboMsg);
                            webSocketClient.send(weiboMessage);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private String getRegex(String text) {
            String url = getRegex(text, "https://weibo.com/\\d+");
            if (url == null) {
                url = getRegex(text, "https://weibo.com/u/\\d+");
                if (url == null) {
                    url = getRegex(text, "https://m.weibo.cn/profile/\\d+");
                }
            }
            return url;
        }

        private String getRegex(String text, String regex) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                return matcher.group();
            }
            return null;
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
    };
}
