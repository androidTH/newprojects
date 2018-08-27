package cn.liaox.cachelib.bean;

/**
 *
 */

public class UserBean extends CacheBean{
    private String userId;
    private String headUrl;
    private String nickName;
    public UserBean() {
        super();
    }
    public UserBean(long time) {
        super(time);
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

}
