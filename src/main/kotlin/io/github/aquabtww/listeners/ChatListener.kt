package io.github.aquabtww.listeners

import io.github.aquabtww.utils.lpUser
import io.github.aquabtww.utils.parseMini
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ChatListener : Listener {

    @EventHandler
    fun AsyncChatEvent.onChat() {
        val lpUser = player.lpUser ?: return

        renderer { player, _, message, _ ->
            val prefix = lpUser.cachedData.metaData.prefix ?: ""
            val playerName = player.name
            val msg = PlainTextComponentSerializer.plainText().serialize(message)

            "$prefix$playerName <dark_gray>» <gray>$msg".parseMini()
        }
    }

}