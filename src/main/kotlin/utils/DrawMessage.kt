package org.laolittle.plugin.groupconn.utils

import net.mamoe.mirai.contact.User
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.RenderingHints
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import javax.imageio.ImageIO


object DrawMessage {
  fun processMessageImg(avatar: BufferedImage): InputStream {
      val width = 300
      val height = 200
      val messageImage = BufferedImage(width, height, TYPE_INT_ARGB)
      val bgImage = BufferedImage(500, 500, TYPE_INT_ARGB)
      val g2 = messageImage.createGraphics()
      g2.composite = AlphaComposite.Src
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      g2.color = Color.WHITE
      g2.fill(
          RoundRectangle2D.Float(
              0F, 0F, width.toFloat(), height.toFloat(),
              50F, 50F
          )
      )
      g2.dispose()
      val bg2 = bgImage.createGraphics()
      bg2.composite = AlphaComposite.Src
      bg2.drawImage(messageImage, 150, 0, width, height, null)
      bg2.drawImage(avatar, 0, 0, 105, 105, null)
      bg2.dispose()
      val os = ByteArrayOutputStream()
      ImageIO.write(bgImage, "PNG", os)
      return ByteArrayInputStream(os.toByteArray())
  }

    fun getHeadImg(sender: User): BufferedImage {
        val targetSize = 105
        val cornerRadius = 105
        val avatarImage = ImageIO.read(URL(sender.avatarUrl))
        val roundImage = BufferedImage(targetSize, cornerRadius, TYPE_INT_ARGB)
        val g2 = roundImage.createGraphics()
        g2.composite = AlphaComposite.Src
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2.color = Color.WHITE
        g2.fill(
            RoundRectangle2D.Float(
                0F, 0F, targetSize.toFloat(), targetSize.toFloat(),
                cornerRadius.toFloat(), cornerRadius.toFloat()
            )
        )
        g2.composite = AlphaComposite.SrcAtop
        g2.drawImage(avatarImage, 0, 0, targetSize, cornerRadius, null)
        g2.dispose()
        return roundImage
    }
}