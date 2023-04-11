package io.github.aquabtww.stash

import dev.triumphteam.gui.guis.Gui
import io.github.aquabtww.Chambers
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask

class Stash(
    private val stashManager: StashManager,
    private val player: Player
) {

    var timeLeft = 60

    val gui = Gui.gui()
        .rows(6)
        .title(Component.text("STASH | ??"))
        .disableItemTake()
        .disableItemPlace()
        .disableItemSwap()
        .create()

    init {
        Bukkit.getScheduler().runTaskTimer(Chambers.getInstance(), { task: BukkitTask ->
            timeLeft -= 1

            if (gui.inventory.viewers.isNotEmpty())
                gui.updateTitle("STASH | $timeLeft")

            if (timeLeft <= 0) {
                stashManager.deleteStash(player)
                task.cancel()
            }
        }, 1L, 20L)
    }

}