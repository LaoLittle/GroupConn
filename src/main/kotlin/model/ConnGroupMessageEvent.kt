package org.laolittle.plugin.groupconn.model

import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.event.AbstractEvent
import net.mamoe.mirai.message.data.MessageChain

data class ConnGroupMessageEvent(
    val message: MessageChain,
    val sender: User,
    val group: Group,
    val target: Group
) : AbstractEvent()

data class ConnGroupDisconnectEvent(
    val group: Group,
    val sender: User
): AbstractEvent()

val connectedGroups: MutableSet<Group> = mutableSetOf()
// val connectingGroups: MutableSet<Group> = mutableSetOf()