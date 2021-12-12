package org.laolittle.plugin.groupconn

import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.info
import org.laolittle.plugin.groupconn.command.Connect
import org.laolittle.plugin.groupconn.command.List
import org.laolittle.plugin.groupconn.model.ConnEvent

object GroupConn : KotlinPlugin(
    JvmPluginDescription(
        id = "org.laolittle.plugin.groupconn.GroupConn",
        version = "1.0",
        name = "GroupConnector"
    ) {
        author("LaoLittle")
    }
) {
    @OptIn(ConsoleExperimentalApi::class, ExperimentalCommandDescriptors::class)
    override fun onEnable() {
        List.register()
        Connect.register()
        logger.info { "Plugin loaded" }
        GlobalEventChannel.subscribeAlways<ConnEvent> {
            target.sendMessage(buildMessageChain {
                add(sender.nameCardOrNick + "\n")
                add(message)
            })
        }


    }

}