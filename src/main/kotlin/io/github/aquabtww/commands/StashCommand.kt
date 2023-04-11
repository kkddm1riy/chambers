package io.github.aquabtww.commands

import io.github.aquabtww.stash.StashManager
import io.github.aquabtww.utils.parseMini
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class StashCommand(
    private val stashManager: StashManager
) : BaseCommand("stash") {

    override suspend fun onCommand(sender: CommandSender, args: Array<out String>) {
        if (sender !is Player) return

        val stash = stashManager.stashes[sender]
            ?: run {
                sender.sendMessage("<#FF3333> STASH <bold>✒</bold> <#FF9999>You have no items in stash.".parseMini())
                return
            }

        stash.gui.open(sender)
    }

}