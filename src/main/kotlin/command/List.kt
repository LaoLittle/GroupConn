package org.laolittle.plugin.groupconn.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildForwardMessage
import org.laolittle.plugin.groupconn.GroupConn

object List : SimpleCommand(
    GroupConn, "list",
    description = "群列表"
) {
    @Handler
    suspend fun CommandSenderOnMessage<*>.handle() {
        val groups = buildForwardMessage(if (subject is Group) subject!! else user!!) {
            for ((i, group) in bot!!.groups.withIndex()) {
                add(bot!!, PlainText("$i: ${group.name}(${group.id})"))
            }
        }

        getGroupOrNull()?.sendMessage(groups)
    }
}