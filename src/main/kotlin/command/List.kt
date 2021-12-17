package org.laolittle.plugin.groupconn.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildForwardMessage
import org.laolittle.plugin.groupconn.GroupConn

object List : SimpleCommand(
    GroupConn, "list",
    description = "群列表"
) {
    @Handler
    suspend fun CommandSenderOnMessage<*>.handle() {
        val groups = buildForwardMessage(fromEvent.subject) {
            for ((i, group) in fromEvent.bot.groups.withIndex()) {
                add(fromEvent.bot, PlainText("$i: ${group.name}(${group.id})"))
            }
        }

        fromEvent.subject.sendMessage(groups)
    }
}