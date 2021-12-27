package org.laolittle.plugin.groupconn.model

import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.event.AbstractEvent
import net.mamoe.mirai.message.data.MessageChain

data class ConnGroupMessageEvent(
    val message: MessageChain,
    val sender: Member,
    val group: Group,
    val target: Group
) : AbstractEvent()

data class ConnGroupDisconnectEvent(
    val group: Group,
    val sender: Member
) : AbstractEvent()
// val connectingGroups: MutableSet<Group> = mutableSetOf()