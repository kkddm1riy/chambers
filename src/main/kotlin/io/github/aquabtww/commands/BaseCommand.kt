package io.github.aquabtww.commands

import com.github.shynixn.mccoroutine.bukkit.SuspendingCommandExecutor
import com.github.shynixn.mccoroutine.bukkit.SuspendingTabCompleter
import com.github.shynixn.mccoroutine.bukkit.setSuspendingTabCompleter
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

abstract class BaseCommand(
    val name: String,
    hasCompleter: Boolean = false
) : SuspendingCommandExecutor {

    init {
        if (hasCompleter) registerCompleter()
    }
    private fun registerCompleter() {
        Bukkit.getServer().getPluginCommand(name)?.setSuspendingTabCompleter(this as? SuspendingTabCompleter ?: return)
    }

    override suspend fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        onCommand(sender, args)
        return true
    }

    abstract suspend fun onCommand(sender: CommandSender, args: Array<out String>)

}