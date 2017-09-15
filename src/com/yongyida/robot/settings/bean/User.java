package com.yongyida.robot.settings.bean;

public class User {
    private long id = 0;
    /**
     * 姓名
     */
    private String name = "";
    /**
     * 昵称
     */
    private String nickName = "";
    /**
     * 控制者
     */
    private long controller = 0;
    /**
     * 是否在线
     */
    private boolean online = false;

    public User(){}

    public User(long id, String name, String nickName, long controller, boolean online){
        if (id != 0) {
            this.id = id;
        }
        if (name != null && !name.isEmpty()) {
            this.name = name;
        }
        if (nickName != null && !nickName.isEmpty()) {
            this.nickName = nickName;
        }
        this.controller = controller;
        this.online = online;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getController() {
        return controller;
    }

    public void setController(Long controller) {
        this.controller = controller;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public String toString() {
        return "\nUser [id=" + id + ", name=" + name
                + ", nickName=" + nickName + ", controller=" +
                controller + ", online="+ online + "]";
    }
}
