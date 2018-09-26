package z.houbin.kq.weibo;

import org.json.JSONObject;

public interface WeiboCallBack {
    void onSuccess(WeiboResult result);

    void onFailed(WeiboResult result);
}
