package com.yongyida.robot.settings.utils;

import com.yongyida.robot.settings.utils.log.LogTool;
import com.yongyida.robot.settings.bean.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class JsonUtils {
    private static final String TAG = JsonUtils.class.getSimpleName();

    public static List<User> getUser(String json) {
        List<User> list = new ArrayList<User>();
        try {
            JSONObject joResult = new JSONObject(json);
            LogTool.showLog(TAG, "getUser", "jsonResult" + joResult);
            if (joResult.has("Users")) {
                JSONArray array = new JSONArray(joResult.optString("Users"));
                if (array != null) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        long id = jsonObject.getLong("id");
                        String name = jsonObject.optString("name");
                        String nick = jsonObject.optString("nickname");
                        long controller = jsonObject.getLong("controller");
                        boolean online = jsonObject.optBoolean("online");
                        User user = new User(id, name, nick, controller, online);
                        list.add(user);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;

    }

}
