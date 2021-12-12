package org.laolittle.plugin.groupconn.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.BotIsBeingMutedException
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.isOperator
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.broadcast
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.content
import org.laolittle.plugin.groupconn.GroupConn
import org.laolittle.plugin.groupconn.model.ConnEvent
import org.laolittle.plugin.groupconn.model.activeGroups

@ConsoleExperimentalApi
@ExperimentalCommandDescriptors
object Connect : SimpleCommand(
    GroupConn, "conn",
    description = "连接两个群"
){

    override val prefixOptional: Boolean = true //命令可免去斜杠

    @Handler
    suspend fun CommandSenderOnMessage<*>.handle(target: Group){
        if (target == getGroupOrNull()) {
            getGroupOrNull()?.sendMessage("不能连线同一个群！")
            return
        }
        if (activeGroups.contains(getGroupOrNull())){
            getGroupOrNull()?.sendMessage("本群已开启连线！请勿重复开启")
            return
        }
        if (activeGroups.contains(target)){
            getGroupOrNull()?.sendMessage("目标群已开启连线！")
            return
        }
       try {
            target.sendMessage("有来自群 ${getGroupOrNull()?.name}(${getGroupOrNull()?.id}) 的申请！管理员发送同意即可开始连线")
        } catch (e: BotIsBeingMutedException){
            getGroupOrNull()?.sendMessage("我在那个群被禁言了...无法发送申请")
            return
        }
        getGroupOrNull()?.sendMessage("正在等待目标群 ${target.name} 同意...")

        GlobalEventChannel.subscribeAlways<GroupMessageEvent> {
            if (activeGroups.contains(subject)) return@subscribeAlways
            if (message.content == "同意") {
                if (sender.isOperator()){
                    activeGroups.add(getGroupOrNull()!!)
                    activeGroups.add(target)
                    GlobalEventChannel.subscribeAlways<GroupMessageEvent> {
                        when (subject){
                            getGroupOrNull() ->{
                                val event = ConnEvent(message, sender, getGroupOrNull()!!, target)
                                event.broadcast()
                            }
                            target -> {
                                val event = ConnEvent(message, sender, target, getGroupOrNull()!!)
                                event.broadcast()
                            }
                        }
                    }
                    getGroupOrNull()?.sendMessage("目标群已同意")
                    target.sendMessage("已开启连线")
                }
                else {
                    subject.sendMessage("权限不足！")
                }
            }
        }
    }
}