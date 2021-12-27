package org.laolittle.plugin.groupconn

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

/*

object GroupConnConfig : AutoSavePluginConfig("GroupConnConfig") {
    @ValueDescription("消息记录额外经过的群聊 (群号)")
    val middleGroup: Long? by value(null)
    val a: MutableMap<String, String> by value()
}


 */
object GroupConnConfig : AutoSavePluginConfig("GroupConnConfig") {
    @ValueDescription(
        """
        跨群聊天消息格式
        变量: 
        %昵称%, %头衔%, %号码%, %消息%, %发送群名%, %发送群号%, %接收群名%, %接收群号%
    """
    )
    val model by value("%昵称%\n%消息%")
}