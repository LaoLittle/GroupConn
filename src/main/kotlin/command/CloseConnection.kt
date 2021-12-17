package org.laolittle.plugin.groupconn.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.isOperator
import org.laolittle.plugin.groupconn.GroupConn
import org.laolittle.plugin.groupconn.model.activeGroups

object CloseConnection : SimpleCommand(
    GroupConn, "disconnect", "dc", "close", "关闭连接",
    description = "连接两个群"
) {

    @OptIn(ConsoleExperimentalApi::class, ExperimentalCommandDescriptors::class)
    override val prefixOptional: Boolean = true

    @Handler
    suspend fun CommandSenderOnMessage<*>.handle() {
        val group = getGroupOrNull()
        if (group == null) {
            subject?.sendMessage("请在群聊下执行此命令！")
            return
        }
        if (!activeGroups.contains(group)) {
            group.sendMessage("当前群聊并未有任何连接")
            return
        }
        val sender = fromEvent.sender as Member
        if (sender.isOperator()) {
            val target = activeGroups[group]
            activeGroups.remove(group)
            activeGroups.remove(target)
            group.sendMessage("已关闭 ${target?.name} 的连接")
            target?.sendMessage("群 ${group.name} 主动关闭了一个现有连接")
        } else group.sendMessage("仅管理员能够关闭连接！")
    }
}