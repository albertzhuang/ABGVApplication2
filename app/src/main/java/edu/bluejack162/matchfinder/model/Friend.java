package edu.bluejack162.matchfinder.model;

/**
 * Created by alber on 04/07/2017.
 */

public class Friend {
    private String userAddId;
    private String friendId;
    private String frienName;
    private String friendProfile;

    public Friend(String userAddId, String friendId, String frienName, String friendProfile) {
        this.userAddId = userAddId;
        this.friendId = friendId;
        this.frienName = frienName;
        this.friendProfile = friendProfile;
    }

    public String getFrienName() {
        return frienName;
    }

    public void setFrienName(String frienName) {
        this.frienName = frienName;
    }

    public String getFriendProfile() {
        return friendProfile;
    }

    public void setFriendProfile(String friendProfile) {
        this.friendProfile = friendProfile;
    }

    public Friend() {}


    public String getUserAddId() {
        return userAddId;
    }

    public void setUserAddId(String userAddId) {
        this.userAddId = userAddId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }
}
