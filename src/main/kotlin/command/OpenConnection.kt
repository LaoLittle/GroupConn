package org.laolittle.plugin.groupconn.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.BotIsBeingMutedException
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.isOperator
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.ListeningStatus
import net.mamoe.mirai.event.broadcast
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.content
import org.laolittle.plugin.groupconn.GroupConn
import org.laolittle.plugin.groupconn.model.ConnGroupMessageEvent
import org.laolittle.plugin.groupconn.model.activeGroups

@ConsoleExperimentalApi
@ExperimentalCommandDescriptors
object OpenConnection : SimpleCommand(
    GroupConn, "conn", "connect", "open", "连接",
    description = "连接两个群"
) {
    override val prefixOptional: Boolean = true //命令可免去斜杠

    @Handler
    suspend fun CommandSenderOnMessage<*>.handle(target: Group) {
        if (getGroupOrNull() == null) {
            subject?.sendMessage("请在群聊下执行此命令！")
            return
        }
        if (target == getGroupOrNull()) {
            getGroupOrNull()?.sendMessage("不能连线同一个群！")
            return
        }
        if (activeGroups.contains(getGroupOrNull())) {
            getGroupOrNull()?.sendMessage("本群已开启连线！请勿重复开启")
            return
        }
        if (activeGroups.contains(target)) {
            getGroupOrNull()?.sendMessage("目标群已开启连线！")
            return
        }
        try {
            target.sendMessage("有来自群 ${getGroupOrNull()?.name}(${getGroupOrNull()?.id}) 的申请！管理员发送同意即可开始连线")
        } catch (e: BotIsBeingMutedException) {
            getGroupOrNull()?.sendMessage("我在那个群被禁言了...无法发送申请")
            return
        }
        getGroupOrNull()?.sendMessage("正在等待目标群 ${target.name} 同意...")
        GlobalEventChannel.subscribe<GroupMessageEvent> {
            if (subject == target)
                if (message.content == "同意") {
                    if (sender.isOperator()) {
                        activeGroups[getGroupOrNull()!!] = target
                        activeGroups[target] = getGroupOrNull()!!
                        GlobalEventChannel.subscribe<GroupMessageEvent> Here@{
                            if (!(activeGroups.contains(getGroupOrNull()) || activeGroups.contains(target))) return@Here ListeningStatus.STOPPED
                            when (subject) {
                                getGroupOrNull() -> {
                                    val event = ConnGroupMessageEvent(message, sender, getGroupOrNull()!!, target)
                                    event.broadcast()
                                }
                                target -> {
                                    val event = ConnGroupMessageEvent(message, sender, target, getGroupOrNull()!!)
                                    event.broadcast()
                                }
                            }
                            ListeningStatus.LISTENING
                        }
                        getGroupOrNull()?.sendMessage("目标群已同意，发送 \"dc\" 可断开连线")
                        target.sendMessage("已开启连线，发送 \"dc\" 可断开连线")
                        return@subscribe ListeningStatus.STOPPED
                    } else {
                        subject.sendMessage("权限不足！")
                    }
                }
            ListeningStatus.LISTENING
        }
    }
}