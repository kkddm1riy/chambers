package io.github.aquabtww.stash

import dev.triumphteam.gui.builder.item.ItemBuilder
import io.github.aquabtww.utils.parseMini
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

class StashManager {

    private val _stashes: MutableMap<Player, Stash> = mutableMapOf()
    val stashes: Map<Player, Stash>
        get() = Collections.unmodifiableMap(_stashes)

    fun updateStashTime(player: Player, newTime: Int = 60) {
        _stashes[player]?.timeLeft = newTime
    }

    fun createStash(player: Player) {
        println("created new stash for ${player.name}")
        val stash = Stash(this, player)
        _stashes[player] = stash
    }

    fun addToStash(player: Player, itemStack: ItemStack) {
        val stash = _stashes[player]
            ?: return

        val item = ItemBuilder.from(itemStack).asGuiItem()
        item.setAction {
            if (player.inventory.firstEmpty() != -1) {
                stash.gui.removeItem(item)
                player.inventory.addItem(itemStack)
            } else {
                player.sendMessage("<#FF3333> STASH <bold>✒</bold> <#FF9999>Your inventory is full.".parseMini())
            }

        }

        stash.gui.addItem(item)
    }

    fun deleteStash(player: Player) {
        val stash = _stashes[player]
            ?: return

        println("deleted stash for ${player.name}")

        stash.gui.inventory.viewers.forEach { stash.gui.close(it) }
        _stashes.remove(player)
    }

}