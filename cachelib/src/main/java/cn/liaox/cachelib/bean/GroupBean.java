package cn.liaox.cachelib.bean;

/**
 *
 */

public class GroupBean extends CacheBean{
    public String groupId;
    public String groupHeadPic;
    public String groupName;
    public int iGroupNum;
    public String sGroupDesc;
    public int  iGroupStatus;
    public GroupBean() {
        super();
    }
    public GroupBean(long time) {
        super(time);
    }

    public String getGroupId() {
        return groupId == null ? "" : groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupHeadPic() {
        return groupHeadPic == null ? "" : groupHeadPic;
    }

    public void setGroupHeadPic(String groupHeadPic) {
        this.groupHeadPic = groupHeadPic;
    }

    public String getGroupName() {
        return groupName == null ? "" : groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getiGroupNum() {
        return iGroupNum;
    }

    public void setiGroupNum(int iGroupNum) {
        this.iGroupNum = iGroupNum;
    }

    public String getsGroupDesc() {
        return sGroupDesc == null ? "" : sGroupDesc;
    }

    public void setsGroupDesc(String sGroupDesc) {
        this.sGroupDesc = sGroupDesc;
    }

    public int getiGroupStatus() {
        return iGroupStatus;
    }

    public void setiGroupStatus(int iGroupStatus) {
        this.iGroupStatus = iGroupStatus;
    }
}
