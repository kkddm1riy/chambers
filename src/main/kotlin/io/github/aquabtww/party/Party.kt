package io.github.aquabtww.party

import io.github.aquabtww.database.models.CPlayer

data class Party(
    val members: MutableList<PartyMember>
) {
    val cPlayerMembers
        get() = members.map { it.cPlayer }
    val bukkitPlayerMembers
        get() = members.map { it.bukkitPlayer }
    val size
        get() = members.size

    fun getRole(cPlayer: CPlayer) = members.firstOrNull { it.cPlayer == cPlayer }?.role
    fun findByCPlayer(cPlayer: CPlayer) = members.firstOrNull { it.cPlayer == cPlayer }
}

data class PartyMember(
    val cPlayer: CPlayer,
    var role: PartyRole
) {
    val bukkitPlayer = cPlayer.bukkitPlayer
}

enum class PartyRole {
    LEADER,
    MODERATOR,
    DEFAULT
}