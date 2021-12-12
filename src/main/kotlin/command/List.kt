package org.laolittle.plugin.groupconn.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.contact.Group
import org.laolittle.plugin.groupconn.GroupConn

object List : SimpleCommand(
    GroupConn, "list",
    description = "群列表"
) {
    @Handler
    suspend fun CommandSenderOnMessage<*>.handle(){
        var groups = ""
        for ((i, group) in bot!!.groups.withIndex()){
            groups += "$i: ${group.name}(${group.id})"
        }
        getGroupOrNull()?.sendMessage(groups)
    }
}