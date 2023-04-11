package io.github.aquabtww.mythicmobs.mechanics

import io.github.aquabtww.database.managers.CPlayerManager
import io.github.aquabtww.items.CItemManager
import io.github.aquabtww.stash.StashManager
import io.github.aquabtww.utils.parseMini
import io.github.aquabtww.utils.valueOfOrNull
import io.lumine.mythic.api.adapters.AbstractEntity
import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.skills.ITargetedEntitySkill
import io.lumine.mythic.api.skills.SkillMetadata
import io.lumine.mythic.api.skills.SkillResult
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.entity.Player

class RewardMechanic(
    private val config: MythicLineConfig,
    private val playerManager: CPlayerManager,
    private val itemManager: CItemManager,
    private val stashManager: StashManager,
) : ITargetedEntitySkill {

    private val rewardType: String? = config.getString(arrayOf("reward_type", "type"))?.uppercase()

    override fun castAtEntity(data: SkillMetadata?, target: AbstractEntity?): SkillResult {
        val player = (target?.bukkitEntity as? Player)
            ?: return SkillResult.ERROR

        when(valueOfOrNull<RewardType>(rewardType)) {
            RewardType.CURRENCY -> {
                val cPlayer = playerManager.findByUUID(player.uniqueId)
                    ?: return SkillResult.ERROR

                val party = cPlayer.party
                val partyMembers = party?.cPlayerMembers?.filter { it != cPlayer }

                val amount = config.getInteger(arrayOf("amount", "am"))
                if (amount <= 0) {
                    player.sendMessage("Parameter 'amount' is either: not set or less than 1")
                }

                val currencyType = config.getString(arrayOf("currency_type", "currency", "cur"))
                when(currencyType) {
                    "gold" -> {
                        partyMembers?.forEach {
                            it.gold += (amount * (0.1+(0.5/partyMembers.size))).toInt()
                        }
                        cPlayer.gold += amount
                    }
                    "xp" -> {
                        partyMembers?.forEach {
                            it.xp += (amount * (0.1+(0.5/partyMembers.size))).toInt()
                        }
                        cPlayer.xp += amount
                    }
                    "level" -> {
                        partyMembers?.forEach {
                            it.level += (amount * (0.1+(0.5/partyMembers.size))).toInt()
                        }
                        cPlayer.level += amount
                    }
                    else -> {
                        player.sendMessage("Couldn't recognise parameter: 'currency_type'.")
                        return SkillResult.ERROR
                    }
                }
                player.sendActionBar("<#34FF34>You earned $amount $currencyType".parseMini())
            }
            RewardType.ITEM -> {
                val itemID = config.getString(arrayOf("item", "it"))

                val item = itemManager.findByID(itemID)
                    ?: run {
                        player.sendMessage("Couldn't recognise item id: '$itemID'.")
                        return SkillResult.ERROR
                    }

                val sound = Sound.sound(Key.key("block.note_block.flute"), Sound.Source.MASTER, 2f, 1f)
                player.playSound(sound)
                player.sendMessage("<#33FF33> DROP <bold>✒</bold> <#99FF99>You dropped an ".parseMini()
                    .append(item.itemMeta.displayName() ?: "".parseMini())
                    .append("<#99FF99>!".parseMini())
                )

                if (player.inventory.firstEmpty() == -1) {
                    if (stashManager.stashes[player] == null) {
                        stashManager.createStash(player)
                    } else stashManager.updateStashTime(player)

                    stashManager.addToStash(player, item)
                    player.sendMessage("<#33FF33> DROP <bold>✒</bold> <#99FF99>Your inventory is full, putting it into your stash...".parseMini())
                    return SkillResult.SUCCESS
                }

                player.inventory.addItem(item)
            }
            else -> {
                player.sendMessage("Couldn't recognise parameter: 'reward_type'.")
                return SkillResult.ERROR
            }
        }

        return SkillResult.SUCCESS
    }

    enum class RewardType {
        CURRENCY,
        ITEM
    }

}