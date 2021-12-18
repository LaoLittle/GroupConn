package org.laolittle.plugin.groupconn.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.isOperator
import net.mamoe.mirai.event.broadcast
import net.mamoe.mirai.utils.error
import org.laolittle.plugin.groupconn.GroupConn
import org.laolittle.plugin.groupconn.model.ConnGroupDisconnectEvent
import org.laolittle.plugin.groupconn.model.connectedGroups

object CloseConnection : SimpleCommand(
    GroupConn, "disconnect", "dc", "close", "关闭连接",
    description = "连接两个群"
) {

    @OptIn(ConsoleExperimentalApi::class, ExperimentalCommandDescriptors::class)
    override val prefixOptional: Boolean = true

    @Handler
    suspend fun CommandSenderOnMessage<*>.handle() {
        val group = fromEvent.subject
        if (group !is Group) {
            subject?.sendMessage("请在群聊下执行此命令！") ?: GroupConn.logger.error { "请在群聊下执行此命令！" }
            return
        }
        if (!connectedGroups.contains(group)) {
            group.sendMessage("当前群聊并未有任何连接")
            return
        }
        val sender = fromEvent.sender as Member
        if (sender.isOperator()) {
            val disconnectEvent = ConnGroupDisconnectEvent(group, sender)
            disconnectEvent.broadcast()
        } else group.sendMessage("仅管理员能够关闭连接！")
    }
}