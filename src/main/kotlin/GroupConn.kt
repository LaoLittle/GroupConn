package org.laolittle.plugin.groupconn

import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.ListeningStatus
import net.mamoe.mirai.event.broadcast
import net.mamoe.mirai.event.events.MessageRecallEvent.GroupRecall
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.info
import org.laolittle.plugin.groupconn.command.CloseConnection
import org.laolittle.plugin.groupconn.command.List
import org.laolittle.plugin.groupconn.command.OpenConnection
import org.laolittle.plugin.groupconn.model.ConnGroupMessageEvent
import org.laolittle.plugin.groupconn.model.ConnGroupRecallEvent
import org.laolittle.plugin.groupconn.utils.DrawMessage.getHeadImg
import org.laolittle.plugin.groupconn.utils.DrawMessage.processMessageImg

object GroupConn : KotlinPlugin(
    JvmPluginDescription(
        id = "org.laolittle.plugin.groupconn.GroupConn",
        version = "1.0.1",
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
            "teea" {
                subject.sendImage(processMessageImg(getHeadImg(sender)))
            }
        }
        GlobalEventChannel.subscribeAlways<ConnGroupMessageEvent> {
         val sentOutMessage = target.sendMessage(buildMessageChain {
                add(sender.nameCardOrNick + "\n")
                add(message.toMessageChain())
            })
            GlobalEventChannel.subscribe<GroupRecall> {
                if ((messageIds.contentEquals(message.ids))&&(messageInternalIds.contentEquals(message.internalId))&&(messageTime == message.time)){
                    val recallEvent = ConnGroupRecallEvent(sentOutMessage)
                    recallEvent.broadcast()
                    return@subscribe ListeningStatus.STOPPED
                }
                ListeningStatus.LISTENING
            }
        }
        GlobalEventChannel.subscribeAlways<ConnGroupRecallEvent> {
            targetMessage.recall()
        }
    }
}