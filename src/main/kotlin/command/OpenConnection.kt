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
        val group = getGroupOrNull()
        if (group == null) {
            fromEvent.subject.sendMessage("请在群聊下执行此命令！")
            return
        }
        if (target == group) {
            group.sendMessage("不能连线同一个群！")
            return
        }
        if (activeGroups.contains(group)) {
            group.sendMessage("本群已开启连线！请勿重复开启")
            return
        }
        if (activeGroups.contains(target)) {
            group.sendMessage("目标群已开启连线！")
            return
        }
        try {
            target.sendMessage("有来自群 ${group.name}(${group.id}) 的申请！管理员发送同意即可开始连线")
        } catch (e: BotIsBeingMutedException) {
            group.sendMessage("我在那个群被禁言了...无法发送申请")
            return
        }
        group.sendMessage("正在等待目标群 ${target.name} 同意...")
        GlobalEventChannel.subscribe<GroupMessageEvent> {
            if (subject == target)
                if (message.content == "同意") {
                    if (sender.isOperator()) {
                        activeGroups[group] = target
                        activeGroups[target] = group
                        GlobalEventChannel.subscribe<GroupMessageEvent> Here@{
                            if (!(activeGroups.contains(group) || activeGroups.contains(target))) return@Here ListeningStatus.STOPPED
                            when (subject) {
                                group -> {
                                    val event = ConnGroupMessageEvent(message, sender, group, target)
                                    event.broadcast()
                                }
                                target -> {
                                    val event = ConnGroupMessageEvent(message, sender, target, group)
                                    event.broadcast()
                                }
                            }
                            ListeningStatus.LISTENING
                        }
                        group.sendMessage("目标群已同意，发送 \"dc\" 可断开连线")
                        target.sendMessage("已开启连线，发送 \"dc\" 可断开连线")
                        return@subscribe ListeningStatus.STOPPED
                    } else subject.sendMessage("权限不足！")
                }
            ListeningStatus.LISTENING
        }

    }
}