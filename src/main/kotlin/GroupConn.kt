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
import net.mamoe.mirai.message.code.MiraiCode.deserializeMiraiCode
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
        version = "1.1",
        name = "GroupConnector"
    ) {
        author("LaoLittle")
    }
) {
    @OptIn(ConsoleExperimentalApi::class, ExperimentalCommandDescriptors::class)
    override fun onEnable() {
        GroupConnConfig.reload()
        List.register()
        OpenConnection.register()
        CloseConnection.register()
        logger.info { "跨群聊天初始化完成" }
        GlobalEventChannel.subscribeAlways<ConnGroupMessageEvent> {
            /*    val targetMessageSource = quotableMessage[message[QuoteReply]?.source]
              group.sendMessage("""
                   ${message[QuoteReply]?.source}
                   $targetMessageSource
                   ${quotableMessage[message[QuoteReply]?.source]}
                   """.trimIndent())
             */
            val messageModel = GroupConnConfig.model
                .replace("%昵称%", sender.nameCardOrNick)
                .replace("%头衔%", sender.specialTitle)
                .replace("%号码%", sender.id.toString())
                .replace("%消息%", message.serializeToMiraiCode())
                .replace("%发送群名%", group.name)
                .replace("%发送群号%", group.id.toString())
                .replace("%接收群名%", target.name)
                .replace("%接收群号%", target.id.toString())
            val sentOutMessage = target.sendMessage(messageModel.deserializeMiraiCode())
            /*buildMessageChain {
                add(sender.nameCardOrNick + "\n")
                add(message)
                /*  if (targetMessageSource != null){
                      when (targetMessageSource){
                          message.source -> add(targetMessageSource.quote())
                          else -> add(message.quote())
                      }
                  } */

            }
            */

            //   quotableMessage[message.source] = sentOutMessage.source

            val recallEvent = GlobalEventChannel.subscribe<GroupRecall> {
                if ((messageIds.contentEquals(message.ids)) && (messageInternalIds.contentEquals(message.internalId)) && (messageTime == message.time)) {
                    sentOutMessage.recall()
                    //   quotableMessage.remove(message.source)
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

    override fun onDisable() {
        logger.info { "所有跨群聊天已自动断开" }
    }
}