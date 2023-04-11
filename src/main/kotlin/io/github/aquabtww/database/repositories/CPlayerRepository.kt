package io.github.aquabtww.database.repositories

import io.github.aquabtww.database.models.CPlayer
import io.github.aquabtww.utils.parseMini
import kotlinx.coroutines.*
import org.bukkit.Bukkit
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.util.*

class CPlayerRepository(
    database: CoroutineDatabase,
) {

    private val collection = database.getCollection<CPlayer>("players")

    suspend fun findByUUID(uuid: UUID): CPlayer? {
        return collection.findOneById(uuid)
    }

    suspend fun add(uuid: UUID): CPlayer {
        val cPlayer = CPlayer(uuid)
        CoroutineScope(Dispatchers.IO).launch {
            collection.insertOne(cPlayer)
            collection.save(cPlayer)
        }
        return cPlayer
    }

    suspend fun delete(uuid: UUID) {
        Bukkit.getPlayer(uuid)?.kick("<#FF3434>You're getting wiped.".parseMini())
        collection.deleteOneById(uuid)
    }

    suspend fun update(cPlayer: CPlayer) {
        CoroutineScope(Dispatchers.IO).launch {
            collection.replaceOneById(cPlayer.uuid.toString(), cPlayer)
            collection.save(cPlayer)
        }
    }

    suspend fun drop() {
        Bukkit.getServer().setWhitelist(true)

        Bukkit.getOnlinePlayers().forEach {
            it.kick("<#FF3434>Server is wiping...".parseMini())
        }
        withContext(Dispatchers.IO) {
            collection.drop()
        }

        Bukkit.getServer().setWhitelist(false)
    }

}