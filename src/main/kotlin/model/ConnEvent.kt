package org.laolittle.plugin.groupconn.model

import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.event.AbstractEvent
import net.mamoe.mirai.message.data.Message

class ConnEvent(
    val message: Message,
    val sender: User,
    val group: Group,
    val target: Group
) : AbstractEvent()

val activeGroups: MutableMap<Group, Group> = mutableMapOf()