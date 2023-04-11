package io.github.aquabtww.commands

import com.github.shynixn.mccoroutine.bukkit.SuspendingTabCompleter
import io.github.aquabtww.items.CItemManager
import io.github.aquabtww.utils.checkPermission
import io.github.aquabtww.utils.parseMini
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ChambersCommand(
    private val itemManager: CItemManager,
) : BaseCommand("chambers", hasCompleter = true), SuspendingTabCompleter {
    override suspend fun onCommand(sender: CommandSender, args: Array<out String>) {
        if (sender is Player && !sender.checkPermission("chambers.chambers")) {
            sender.sendMessage("<#FF3333> CHAMBERS <bold>✒</bold> <#FF9999>You can't use this command.".parseMini())
            return
        }

        if (args.isEmpty()) {
            sender.sendMessage("<#FF3333> CHAMBERS <bold>✒</bold> <#FF9999>Invalid arguments.".parseMini())
            return
        }
        if (args[0] == "reload") {
            itemManager.reload()
            sender.sendMessage("<#33FF33> CHAMBERS <bold>✒</bold> <#99FF99>Successfully reloaded the plugin.".parseMini())
        }
        if (args[0] == "getitem") {
            if (sender !is Player) return
            if (args.size < 2) {
                sender.sendMessage("<#FF3333> CHAMBERS <bold>✒</bold> <#FF9999>Invalid arguments.".parseMini())
                return
            }

            val item = itemManager.findByID(args[1])
                ?: run {
                    sender.sendMessage("<#FF3333> CHAMBERS <bold>✒</bold> <#FF9999>Couldn't find an item with such id.".parseMini())
                    return
                }
            sender.inventory.addItem(item)
            sender.sendMessage("<#33FF33> CHAMBERS <bold>✒</bold> <#99FF99>Gave you 1 of '${args[1]}'".parseMini())
        }
    }

    override suspend fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (sender is Player && !sender.checkPermission("chambers.chambers")) return emptyList()

        if (args.size == 1) {
            return listOf("getitem", "reload")
        }
        if (args.size == 2 && args[0] == "getitem") {
            return itemManager.items.map { it.id }
        }

        return emptyList()
    }
}