package com.yongyida.robot.settings.bean;

public class SettingsListItem {
    /**
     * Title of settings list item
     */
    private String mTitle;
    /**
     * Image of settings list item
     */
    private int mImageID;
    /**
     * ID of item
     */
    private int itemID;

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public SettingsListItem() {

    }

    /**
     * @param title   Title of settings item
     * @param imageID Resource id of settings icon
     */
    public SettingsListItem(String title, int imageID, int itemID) {
        this.mTitle = title;
        this.mImageID = imageID;
        this.itemID = itemID;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public int getmImageID() {
        return mImageID;
    }

    public void setmImageID(int mImageID) {
        this.mImageID = mImageID;
    }
}
