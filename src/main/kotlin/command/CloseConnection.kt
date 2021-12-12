package org.laolittle.plugin.groupconn.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
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
        if (getGroupOrNull() == null) {
            subject?.sendMessage("请在群聊下执行此命令！")
            return
        }
        if (activeGroups.contains(getGroupOrNull())){
            val target = activeGroups[getGroupOrNull()]
            activeGroups.remove(getGroupOrNull())
            activeGroups.remove(target)
            getGroupOrNull()?.sendMessage("已关闭 ${target?.name} 的连接")
            target?.sendMessage("群 ${getGroupOrNull()?.name} 主动关闭了现有的连接")
        }
        else {
            getGroupOrNull()?.sendMessage("此群尚未任何连接")
        }
    }
}