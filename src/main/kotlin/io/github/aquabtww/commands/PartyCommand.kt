package io.github.aquabtww.commands

import com.github.shynixn.mccoroutine.bukkit.SuspendingTabCompleter
import io.github.aquabtww.database.managers.CPlayerManager
import io.github.aquabtww.database.models.CPlayer
import io.github.aquabtww.party.Party
import io.github.aquabtww.party.PartyMember
import io.github.aquabtww.party.PartyRole
import io.github.aquabtww.utils.parseMini
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PartyCommand(
    private val playerManager: CPlayerManager
) : BaseCommand("party", hasCompleter = true), SuspendingTabCompleter {

    private val invites: MutableMap<CPlayer, Pair<Player, Party>> = mutableMapOf()

    override suspend fun onCommand(sender: CommandSender, args: Array<out String>) {
        val player = sender as? Player ?: return
        val cPlayer = playerManager.findByUUID(player.uniqueId)
            ?: return

        if (args.isEmpty()) {
            sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>Invalid arguments.".parseMini())
            return
        }
        if (args[0] == "create") {
            if (cPlayer.party != null) {
                sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>You are already in a party.".parseMini())
                return
            }
            createParty(cPlayer)
            sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#99FF99>You successfully created a party.".parseMini())
        } else if (args[0] == "disband") {
            if (cPlayer.party == null) {
                sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>You are not in a party.".parseMini())
                return
            }
            if (cPlayer.party?.getRole(cPlayer) != PartyRole.LEADER) {
                sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>You aren't the party leader.".parseMini())
                return
            }
            disbandParty(cPlayer.party!!)
        } else if (args[0] == "leave") {
            if (cPlayer.party == null) {
                sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>You are not in a party.".parseMini())
                return
            }
            val party = leaveParty(cPlayer)
            party?.bukkitPlayerMembers?.forEach {
                it?.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#99FF99>${player.name} left the party.".parseMini())
            }
            sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#99FF99>You successfully left your party.".parseMini())
        } else if (args[0] == "kick") {
            if (args.size < 2) {
                sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>Invalid arguments.".parseMini())
                return
            }
            if (cPlayer.party == null) {
                sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>You are not in a party.".parseMini())
                return
            }
            if (cPlayer.party?.getRole(cPlayer) == PartyRole.DEFAULT) {
                sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>You can't do that.".parseMini())
                return
            }

            val target = Bukkit.getPlayer(args[1])
                ?: run {
                    sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>This player is not online.".parseMini())
                    return
                }

            if (target == player) {
                sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>You can not kick yourself.".parseMini())
                return
            }

            val targetCPlayer = playerManager.findByUUID(target.uniqueId)
                ?: return

            if (cPlayer.party?.cPlayerMembers?.contains(targetCPlayer) == false) {
                sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>This player is not in your party.".parseMini())
                return
            }

            leaveParty(targetCPlayer)
            target.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>You got kicked from your party.".parseMini())
            sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#99FF99>You kicked ${target.name} from party.".parseMini())
        } else if (args[0] == "demote") {
            if (args.size < 2) {
                sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>Invalid arguments.".parseMini())
                return
            }
            if (cPlayer.party == null) {
                sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>You are not in a party.".parseMini())
                return
            }
            if (cPlayer.party?.getRole(cPlayer) != PartyRole.LEADER) {
                sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>You can't do that.".parseMini())
                return
            }

            val target = Bukkit.getPlayer(args[1])
                ?: run {
                    sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>This player is not online.".parseMini())
                    return
                }

            if (target == player) {
                sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>You can not demote yourself.".parseMini())
                return
            }

            val targetCPlayer = playerManager.findByUUID(target.uniqueId)
                ?: return

            if (cPlayer.party?.cPlayerMembers?.contains(targetCPlayer) == false) {
                sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>This player is not in your party.".parseMini())
                return
            }

            demote(targetCPlayer)
            target.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#99FF99>You've been demoted in your party.".parseMini())
            player.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#99FF99>You've demoted ${target.name}.".parseMini())
        } else if (args[0] == "promote") {
            if (args.size < 2) {
                sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>Invalid arguments.".parseMini())
                return
            }
            if (cPlayer.party == null) {
                sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>You are not in a party.".parseMini())
                return
            }
            if (cPlayer.party?.getRole(cPlayer) != PartyRole.LEADER) {
                sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>You can't do that.".parseMini())
                return
            }

            val target = Bukkit.getPlayer(args[1])
                ?: run {
                    sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>This player is not online.".parseMini())
                    return
                }

            if (target == player) {
                sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>You can not promote yourself.".parseMini())
                return
            }

            val targetCPlayer = playerManager.findByUUID(target.uniqueId)
                ?: return

            if (cPlayer.party?.cPlayerMembers?.contains(targetCPlayer) == false) {
                sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>This player is not in your party.".parseMini())
                return
            }

            promote(cPlayer, targetCPlayer)
            target.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#99FF99>You've been promoted in your party.".parseMini())
            player.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#99FF99>You've promoted ${target.name}.".parseMini())
        } else if (args[0] == "invite") {
            if (args.size < 2) {
                sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>Invalid arguments.".parseMini())
                return
            }
            if (cPlayer.party == null) {
                sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>You are not in a party.".parseMini())
                return
            }
            if (cPlayer.party?.getRole(cPlayer) == PartyRole.DEFAULT) {
                sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>You can't do that.".parseMini())
                return
            }

            val target = Bukkit.getPlayer(args[1])
                ?: run {
                    sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>This player is not online.".parseMini())
                    return
                }

            if (target == player) {
                sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>You can not invite yourself.".parseMini())
                return
            }

            val targetCPlayer = playerManager.findByUUID(target.uniqueId)
                ?: return

            if (targetCPlayer.party != null) {
                sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>This player is already in party.".parseMini())
                return
            }

            inviteParty(player, targetCPlayer, cPlayer.party!!)
        } else if (args[0] == "acceptinvite") {
            if (!invites.containsKey(cPlayer)) {
                sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>You haven't been invited.".parseMini())
                return
            }
            val party = invites[cPlayer]!!.second
            cPlayer.party = party
            party.members.add(PartyMember(cPlayer, PartyRole.DEFAULT))
            party.bukkitPlayerMembers.forEach {
                it?.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#99FF99>${player.name} joined the party.".parseMini())
            }
            invites.remove(cPlayer)
        } else if (args[0] == "declineinvite") {
            if (!invites.containsKey(cPlayer)) {
                sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>You haven't been invited.".parseMini())
                return
            }
            val inviter = invites[cPlayer]!!.first
            inviter.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>Declined your party invite.".parseMini())
            invites.remove(cPlayer)
        } else {
            sender.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#FF9999>Invalid arguments.".parseMini())
        }
    }

    override suspend fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        val player = sender as? Player ?: return emptyList()
        val cPlayer = playerManager.findByUUID(player.uniqueId)
            ?: return emptyList()

        if (args.size == 1) {
            return listOf("create", "leave", "disband", "invite", "kick", "promote", "demote", "acceptinvite", "declineinvite")
        }
        if (args.size == 2 && args[0] == "invite") {
            return Bukkit.getOnlinePlayers().map { it.name }
        }
        if (args.size == 2 && args[0] == "kick") {
            return cPlayer.party?.bukkitPlayerMembers?.map { it?.name ?: "" }?.minus(player.name) ?: emptyList()
        }
        if (args.size == 2 && args[0] == "promote") {
            return cPlayer.party?.bukkitPlayerMembers?.map { it?.name ?: "" }?.minus(player.name) ?: emptyList()
        }
        if (args.size == 2 && args[0] == "demote") {
            return cPlayer.party?.bukkitPlayerMembers?.map { it?.name ?: "" }?.minus(player.name) ?: emptyList()
        }
        return emptyList()
    }

    private fun inviteParty(inviter: Player, invitee: CPlayer, party: Party) {
        inviter.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#99FF99>You've invited ${invitee.bukkitPlayer?.name} into your party.".parseMini())
        invitee.bukkitPlayer?.sendMessage(
            "<#FF3333> PARTY <bold>✒</bold> <#99FF99>${inviter.name} invited you into their party. /party acceptinvite to accept the invite."
                .parseMini()
        )
        invites[invitee] = Pair(inviter, party)
    }

    private fun createParty(cPlayer: CPlayer) {
        cPlayer.party = Party(mutableListOf(
            PartyMember(cPlayer, PartyRole.LEADER)
        ))
    }

    private fun disbandParty(party: Party) {
        party.cPlayerMembers.forEach {
            it.bukkitPlayer?.sendMessage("<#FF3333> PARTY <bold>✒</bold> <#99FF99>Your party has been disbanded.".parseMini())
            it.party = null
        }
    }

    private fun leaveParty(cPlayer: CPlayer): Party? {
        val party = cPlayer.party
        if (party?.getRole(cPlayer) == PartyRole.LEADER) {
            disbandParty(party)
        } else {
            cPlayer.party = null
            party?.members?.removeIf { it.cPlayer == cPlayer }
        }
        return party
    }

    private fun promote(leader: CPlayer, cPlayer: CPlayer) {
        val party = cPlayer.party
            ?: return
        when(party.getRole(cPlayer)) {
            PartyRole.MODERATOR -> {
                party.findByCPlayer(leader)?.role = PartyRole.MODERATOR
                party.findByCPlayer(cPlayer)?.role = PartyRole.LEADER
            }
            PartyRole.DEFAULT -> {
                party.findByCPlayer(cPlayer)?.role = PartyRole.MODERATOR
            }
            else -> {}
        }
    }

    private fun demote(cPlayer: CPlayer) {
        val party = cPlayer.party
            ?: return
        when(party.getRole(cPlayer)) {
            PartyRole.MODERATOR -> {
                party.findByCPlayer(cPlayer)?.role = PartyRole.DEFAULT
            }
            else -> {}
        }
    }
}