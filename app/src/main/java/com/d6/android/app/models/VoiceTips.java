package com.d6.android.app.models;

/**
 * author : jinjiarui
 * time   : 2019/11/18
 * desc   :
 * version:
 */
public class VoiceTips {
    private String VoiceChatUName;
    private String VoiceChatContent;
    private String VoiceChatId;
    private int VoiceChatType;
    private int VoiceChatdirection;

    public String getVoiceChatUName() {
        return VoiceChatUName == null ? "" : VoiceChatUName;
    }

    public void setVoiceChatUName(String voiceChatUName) {
        VoiceChatUName = voiceChatUName;
    }

    public String getVoiceChatContent() {
        return VoiceChatContent == null ? "" : VoiceChatContent;
    }

    public void setVoiceChatContent(String voiceChatContent) {
        VoiceChatContent = voiceChatContent;
    }

    public String getVoiceChatId() {
        return VoiceChatId == null ? "" : VoiceChatId;
    }

    public void setVoiceChatId(String voiceChatId) {
        VoiceChatId = voiceChatId;
    }

    public int getVoiceChatType() {
        return VoiceChatType;
    }

    public void setVoiceChatType(int voiceChatType) {
        VoiceChatType = voiceChatType;
    }

    public int getVoiceChatdirection() {
        return VoiceChatdirection;
    }

    public void setVoiceChatdirection(int voiceChatdirection) {
        VoiceChatdirection = voiceChatdirection;
    }
}
