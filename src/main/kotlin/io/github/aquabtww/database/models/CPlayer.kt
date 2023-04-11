package io.github.aquabtww.database.models

import io.github.aquabtww.party.Party
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.bukkit.Bukkit
import java.util.UUID

@Serializable
data class CPlayer(
    @SerialName("_id")
    @Contextual
    val uuid: UUID
) {
    val bukkitPlayer
        get() = Bukkit.getPlayer(uuid)

    var level: Int = 0
    var xp: Int = 0
    var gold: Int = 0

    @Transient
    var party: Party? = null
}
