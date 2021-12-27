package org.laolittle.plugin.groupconn.command

import kotlinx.coroutines.delay
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.BotIsBeingMutedException
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.isOperator
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.ListeningStatus
import net.mamoe.mirai.event.broadcast
import net.mamoe.mirai.event.events.BotMuteEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.utils.error
import org.laolittle.plugin.groupconn.GroupConn
import org.laolittle.plugin.groupconn.model.ConnGroupDisconnectEvent
import org.laolittle.plugin.groupconn.model.ConnGroupMessageEvent
import org.laolittle.plugin.groupconn.model.connectedGroups

@ConsoleExperimentalApi
@ExperimentalCommandDescriptors
object OpenConnection : SimpleCommand(
    GroupConn, "conn", "connect", "open", "连接",
    description = "连接两个群"
) {
    override val prefixOptional: Boolean = true //命令可免去斜杠

    @Handler
    suspend fun CommandSenderOnMessage<*>.handle(target: Group? = null) {
        val group = fromEvent.subject
        if (target == null) {
            group.sendMessage("请输入 ${fromEvent.message.content}+群号 来连接群聊")
            return
        }
        if (group !is Group) {
            group.sendMessage("请在群聊下执行此命令！")
            return
        }
        if (target == group) {
            group.sendMessage("不能连线同一个群！")
            return
        }
        if (connectedGroups.contains(group)) {
            group.sendMessage("本群已开启连线！请勿重复开启")
            return
        }
        if (connectedGroups.contains(target)) {
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
            if (message.content == "同意")
                if (subject == target) {
                    if (sender.isOperator()) {
                        connectedGroups.add(group)
                        connectedGroups.add(target)
                        val openConnListener = GlobalEventChannel.subscribe<GroupMessageEvent> Here@{
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
                        val muteEventListener = GlobalEventChannel.subscribe<BotMuteEvent> Mute@{
                            when (this.group) {
                                group, target -> {
                                    try {
                                        group.sendMessage("我被群${target.name} 的${operator.nameCardOrNick} 禁言了，连接自动关闭")
                                        target.sendMessage("我被群${group.name} 的${operator.nameCardOrNick} 禁言了，连接自动关闭")
                                    } catch (_: BotIsBeingMutedException) {
                                        GroupConn.logger.error { "在群${operator.group}被@${operator.nameCardOrNick}(${operator.id})禁言，群${group.name}(${group.id}) 与群${target.name}(${target.id}) 的连接自动关闭" }
                                    }
                                    val disconnectEvent = ConnGroupDisconnectEvent(group, operator)
                                    disconnectEvent.broadcast()
                                    ListeningStatus.STOPPED
                                }
                                else -> ListeningStatus.LISTENING
                            }
                        }
                        GlobalEventChannel.subscribe<ConnGroupDisconnectEvent> Disconnect@{
                            delay(1_000)
                            when (this.group) {
                                group, target -> {
                                    if (!openConnListener.isCompleted)
                                        openConnListener.complete()
                                    if (!muteEventListener.isCompleted) {
                                        muteEventListener.complete()
                                        when (this.group) {
                                            group -> {
                                                group.sendMessage("已关闭 ${target.name} 的连接")
                                                target.sendMessage("群 ${group.name} 主动关闭了一个现有连接")
                                            }
                                            target -> {
                                                target.sendMessage("已关闭 ${group.name} 的连接")
                                                group.sendMessage("群 ${target.name} 主动关闭了一个现有连接")
                                            }
                                        }
                                    }
                                    connectedGroups.remove(group)
                                    connectedGroups.remove(target)
                                    ListeningStatus.STOPPED
                                }
                                else -> ListeningStatus.LISTENING
                            }
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