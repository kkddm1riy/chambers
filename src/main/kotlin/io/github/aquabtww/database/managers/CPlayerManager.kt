package io.github.aquabtww.database.managers

import io.github.aquabtww.database.models.CPlayer
import io.github.aquabtww.database.repositories.CPlayerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import java.util.*

class CPlayerManager(
    private val playerRepository: CPlayerRepository
) {
    private val _players: MutableSet<CPlayer> = mutableSetOf()
    val players: Set<CPlayer>
        get() = Collections.unmodifiableSet(_players)

    fun findByUUID(uuid: UUID): CPlayer? {
        return _players.firstOrNull { it.uuid == uuid }
    }

    suspend fun findOrCreate(uuid: UUID): CPlayer =
        playerRepository.findByUUID(uuid)
        ?: playerRepository.add(uuid)

    fun add(cPlayer: CPlayer): CPlayer {
        _players.add(cPlayer)
        return cPlayer
    }

    fun remove(uuid: UUID) {
        _players.removeIf { it.uuid == uuid }
    }


    suspend fun scheduleDatabaseUpdate() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                delay(120_000)
                println("Updating all players data in database...")
                Bukkit.getOnlinePlayers().forEach { player ->
                    findByUUID(player.uniqueId)?.let {
                        playerRepository.update(it)
                    }
                }
                println("Updated all players data in database!")
            }
        }
    }

}