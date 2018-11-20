package com.d6.android.app.utils

import android.content.Context
import android.view.View
import com.d6.android.app.activities.UserInfoActivity
import io.rong.imkit.RongIM
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import io.rong.imlib.model.UserInfo
import org.jetbrains.anko.startActivity

/**
 *     author : jinjiarui
 *     time   : 2018/09/01
 *     desc   :
 *     version:
 */
class RongIMConversationClickListener : RongIM.ConversationClickListener{

    /**
     * 当长按用户头像后执行。
     *
     * @param context          上下文。
     * @param conversationType 会话类型。
     * @param user             被点击的用户的信息。
     * @param targetId         会话 id
     * @return 如果用户自己处理了点击后的逻辑处理，则返回 true，否则返回 false，false 走融云默认处理方式。
     */
    override fun onUserPortraitLongClick(p0: Context?, p1: Conversation.ConversationType?, p2: UserInfo?, p3: String?): Boolean {
        return false
    }

    /**
     * 当点击链接消息时执行。
     *
     * @param context 上下文。
     * @param link    被点击的链接。
     * @param message 被点击的消息的实体信息
     * @return 如果用户自己处理了点击后的逻辑处理，则返回 true， 否则返回 false, false 走融云默认处理方式。
     */
    override fun onMessageLinkClick(p0: Context?, p1: String?, p2: Message?): Boolean {
        return false
    }

    /**
     * 当长按消息时执行。
     *
     * @param context 上下文。
     * @param view    触发点击的 View。
     * @param message 被长按的消息的实体信息。
     * @return 如果用户自己处理了长按后的逻辑处理，则返回 true，否则返回 false，false 走融云默认处理方式。
     */
    override fun onMessageLongClick(p0: Context?, p1: View?, p2: Message?): Boolean {
        return false
    }

    /**
     * 当点击用户头像后执行。
     *
     * @param context          上下文。
     * @param conversationType 会话类型。
     * @param user             被点击的用户的信息。
     * @param targetId         会话 id
     * @return 如果用户自己处理了点击后的逻辑处理，则返回 true，否则返回 false，false 走融云默认处理方式。
     */
    @Suppress("UNREACHABLE_CODE")
    override fun onUserPortraitClick(p0: Context?, p1: Conversation.ConversationType?, p2: UserInfo?, p3: String?): Boolean {
        p0!!.startActivity<UserInfoActivity>("id" to p2!!.userId)
        return true
    }

    /**
     * 当点击消息时执行。
     *
     * @param context 上下文。
     * @param view    触发点击的 View。
     * @param message 被点击的消息的实体信息。
     * @return 如果用户自己处理了点击后的逻辑处理，则返回 true， 否则返回 false, false 走融云默认处理方式。
     */
    override fun onMessageClick(p0: Context?, p1: View?, p2: Message?): Boolean {
        return false
    }
}