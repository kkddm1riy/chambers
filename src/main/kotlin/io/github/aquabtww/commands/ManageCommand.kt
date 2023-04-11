package io.github.aquabtww.commands

import com.github.shynixn.mccoroutine.bukkit.SuspendingTabCompleter
import io.github.aquabtww.database.managers.CPlayerManager
import io.github.aquabtww.database.repositories.CPlayerRepository
import io.github.aquabtww.utils.checkPermission
import io.github.aquabtww.utils.parseMini
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

class ManageCommand(
    private val playerRepository: CPlayerRepository,
    private val playerManager: CPlayerManager
) : BaseCommand(name = "adminmanage", hasCompleter = true), SuspendingTabCompleter {

    override suspend fun onCommand(sender: CommandSender, args: Array<out String>) {
        if (sender is Player && !sender.checkPermission("chambers.adminmanage")) {
            sender.sendMessage("<#FF3333> CHAMBERS <bold>✒</bold> <#FF9999>You can't use this command.".parseMini())
            return
        }

        if (args.isEmpty()) {
            sender.sendMessage("<#FF3333> CHAMBERS <bold>✒</bold> <#FF9999>Invalid arguments.".parseMini())
            return
        }
        if (sender is ConsoleCommandSender && args[0] == "drop") {
            playerRepository.drop()
            sender.sendMessage("<#99FF99>Dropped database successfully.".parseMini())
            return
        }

        if (args.size < 2) {
            sender.sendMessage("<#FF3333> CHAMBERS <bold>✒</bold> <#FF9999>Invalid arguments.".parseMini())
            return
        }
        val target = Bukkit.getOfflinePlayer(args[0])
        val targetCPlayer = playerManager.findByUUID(target.uniqueId)
            ?: playerRepository.findByUUID(target.uniqueId)
            ?: run {
                sender.sendMessage("<#FF3333> CHAMBERS <bold>✒</bold> <#FF9999>Couldn't find this player in database.".parseMini())
                return
            }

        if (args[1] == "get") {
            if (args.size < 3) {
                sender.sendMessage("<#FF3333> CHAMBERS <bold>✒</bold> <#FF9999>Invalid arguments.".parseMini())
                return
            }
            val stat = when(args[2]) {
                "uuid" -> targetCPlayer.uuid.toString()
                "level" -> targetCPlayer.level.toString()
                "xp" -> targetCPlayer.xp.toString()
                "gold" -> targetCPlayer.gold.toString()
                else -> {
                    sender.sendMessage("<#FF3333> CHAMBERS <bold>✒</bold> <#FF9999>Invalid arguments.".parseMini())
                    return
                }
            }
            sender.sendMessage("<#33FF33> CHAMBERS <bold>✒</bold> <#99FF99>${args[0]}'s ${args[2]} is $stat.".parseMini())
        } else if (args[1] == "set") {

            if (args.size < 4) {
                sender.sendMessage("<#FF3333> CHAMBERS <bold>✒</bold> <#FF9999>Invalid arguments.".parseMini())
                return
            }

            when(args[2]) {
                "level" -> targetCPlayer.level = args[3].toIntOrNull() ?: run {
                    sender.sendMessage("<#FF3333> CHAMBERS <bold>✒</bold> <#FF9999>'${args[3]}' is not a valid value for ${args[2]}.".parseMini())
                    return
                }
                "xp" -> targetCPlayer.xp = args[3].toIntOrNull() ?: run {
                    sender.sendMessage("<#FF3333> CHAMBERS <bold>✒</bold> <#FF9999>'${args[3]}' is not a valid value for ${args[2]}.".parseMini())
                    return
                }
                "gold" -> targetCPlayer.gold = args[3].toIntOrNull() ?: run {
                    sender.sendMessage("<#FF3333> CHAMBERS <bold>✒</bold> <#FF9999>'${args[3]}' is not a valid value for ${args[2]}.".parseMini())
                    return
                }
                else -> {
                    sender.sendMessage("<#FF3333> CHAMBERS <bold>✒</bold> <#FF9999>Invalid arguments.".parseMini())
                    return
                }
            }
            sender.sendMessage("<#33FF33> CHAMBERS <bold>✒</bold> <#99FF99>Set ${args[0]}'s ${args[2]} to ${args[3]}.".parseMini())
        } else if (args[1] == "wipe") {
            playerRepository.delete(target.uniqueId)
            sender.sendMessage("<#33FF33> CHAMBERS <bold>✒</bold> <#99FF99>Wiped ${args[0]}.".parseMini())
        }

    }

    override suspend fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (sender is Player && !sender.checkPermission("chambers.adminmanage")) return emptyList()

        if (args.size == 1) {
            return Bukkit.getOnlinePlayers().map { it.name }
        }

        if (args.size == 2) {
            return listOf("get", "set", "wipe")
        }

        else if (args.size == 3 && args[1] == "get") {
            return listOf("xp", "level", "gold", "uuid")
        }

        else if (args.size == 3 && args[1] == "set") {
            return listOf("xp", "level", "gold")
        }

        return emptyList()
    }
}