package io.github.aquabtww.listeners

import io.github.aquabtww.database.managers.CPlayerManager
import io.github.aquabtww.database.models.CPlayer
import io.github.aquabtww.database.repositories.CPlayerRepository
import io.github.aquabtww.utils.parseMini
import kotlinx.coroutines.runBlocking
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

class ConnectionListeners(
    private val playerRepository: CPlayerRepository,
    private val playerManager: CPlayerManager,
) : Listener {

    private val pending: MutableMap<UUID, Any?> = mutableMapOf()

    @EventHandler
    fun AsyncPlayerPreLoginEvent.onPreLogin() = runBlocking {
        pending[uniqueId] = playerManager.findOrCreate(uniqueId)
    }

    @EventHandler
    fun PlayerLoginEvent.onLogin() {
        if (pending[player.uniqueId] == null || pending[player.uniqueId] !is CPlayer) {
            disallow(PlayerLoginEvent.Result.KICK_OTHER, "<#FF3333>Unable to fetch player data at login stage.".parseMini())
            return
        }

        playerManager.add(pending[player.uniqueId] as CPlayer)
        pending.remove(player.uniqueId)
    }


    @EventHandler
    suspend fun PlayerQuitEvent.onQuit() {
        playerManager.findByUUID(player.uniqueId)?.let {
            playerRepository.update(it)
        }
        playerManager.remove(player.uniqueId)
    }

}