package io.github.aquabtww.utils

import net.luckperms.api.LuckPerms
import org.bukkit.Bukkit
import org.bukkit.entity.Player

val luckPerms = Bukkit.getServicesManager().getRegistration(LuckPerms::class.java)?.provider

val Player.lpUser
    get() = luckPerms?.getPlayerAdapter(Player::class.java)?.getUser(this)

fun Player.checkPermission(permission: String) : Boolean {
    val lpUser = lpUser ?: return false
    return lpUser.cachedData.permissionData.checkPermission(permission).asBoolean()
}