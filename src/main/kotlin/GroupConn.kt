package org.laolittle.plugin.groupconn

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.ListeningStatus
import net.mamoe.mirai.event.events.MessageRecallEvent.GroupRecall
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.data.ids
import net.mamoe.mirai.message.data.internalId
import net.mamoe.mirai.message.data.time
import net.mamoe.mirai.utils.info
import org.laolittle.plugin.groupconn.command.CloseConnection
import org.laolittle.plugin.groupconn.command.List
import org.laolittle.plugin.groupconn.command.OpenConnection
import org.laolittle.plugin.groupconn.model.ConnGroupMessageEvent

object GroupConn : KotlinPlugin(
    JvmPluginDescription(
        id = "org.laolittle.plugin.groupconn.GroupConn",
        version = "1.0.2",
        name = "GroupConnector"
    ) {
        author("LaoLittle")
    }
) {
    @OptIn(ConsoleExperimentalApi::class, ExperimentalCommandDescriptors::class)
    override fun onEnable() {
        List.register()
        OpenConnection.register()
        CloseConnection.register()
        logger.info { "跨群聊天初始化完成" }
        GlobalEventChannel.subscribeGroupMessages {
        }
        GlobalEventChannel.subscribeAlways<ConnGroupMessageEvent> {
            val sentOutMessage = target.sendMessage(buildMessageChain {
                add(sender.nameCardOrNick + "\n")
                add(message)
            })
            val recallEvent = GlobalEventChannel.subscribe<GroupRecall> {
                if ((messageIds.contentEquals(message.ids)) && (messageInternalIds.contentEquals(message.internalId)) && (messageTime == message.time)) {
                    sentOutMessage.recall()
                    return@subscribe ListeningStatus.STOPPED
                }
                ListeningStatus.LISTENING
            }
            this@GroupConn.launch {
                delay(120_000)
                if (!recallEvent.isCompleted)
                    recallEvent.complete()
            }
        }
    }
}