package io.github.aquabtww.listeners

import io.github.aquabtww.database.managers.CPlayerManager
import io.github.aquabtww.items.CItemManager
import io.github.aquabtww.mythicmobs.mechanics.RewardMechanic
import io.github.aquabtww.stash.StashManager
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class MythicMobListeners(
    private val playerManager: CPlayerManager,
    private val itemManager: CItemManager,
    private val stashManager: StashManager
) : Listener {

    @EventHandler
    fun MythicMechanicLoadEvent.onLoadMechanics() {
        if (mechanicName.equals("reward", true)) {
            register(RewardMechanic(config, playerManager, itemManager, stashManager))
        }
    }
}