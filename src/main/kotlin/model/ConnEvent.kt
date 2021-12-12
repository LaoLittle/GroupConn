package org.laolittle.plugin.groupconn.model

import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.event.AbstractEvent
import net.mamoe.mirai.message.data.Message

class ConnEvent(
    var message: Message,
    var sender: User,
    var group: Group,
    var target: Group
) : AbstractEvent()

val activeGroups: MutableSet<Group> = mutableSetOf()