package io.github.aquabtww.items

import io.github.aquabtww.utils.parseMini
import io.github.aquabtww.utils.setPersistentData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

@Serializable
data class CItem(
    val id: String,
    val material: Material,
    @SerialName("display_name")
    val displayName: String,
    val lore: List<String> = emptyList(),
    @SerialName("persistent_data")
    val persistentData: Map<String, String>? = null,
    @SerialName("is_glowing")
    val isGlowing: Boolean = false,
) {
    fun toItemStack(): ItemStack {
        val item = ItemStack(material)
        item.editMeta { meta ->
            meta.displayName(displayName.parseMini())
            meta.lore(lore.map { it.parseMini() })

            if (isGlowing) meta.addEnchant(Enchantment.MENDING, 1, true)

            meta.isUnbreakable = true
            meta.addItemFlags(*ItemFlag.values())
        }
        persistentData?.forEach { (key, value) ->
            item.setPersistentData(key, value)
        }
        return item
    }
}