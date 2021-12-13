package org.laolittle.plugin.groupconn.model

import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.event.AbstractEvent
import net.mamoe.mirai.message.MessageReceipt
import net.mamoe.mirai.message.data.MessageChain

class ConnGroupMessageEvent(
    val message: MessageChain,
    val sender: User,
    val group: Group,
    val target: Group
) : AbstractEvent()

class ConnGroupRecallEvent(
    val targetMessage: MessageReceipt<Group>,
) : AbstractEvent()

val activeGroups: MutableMap<Group, Group> = mutableMapOf()