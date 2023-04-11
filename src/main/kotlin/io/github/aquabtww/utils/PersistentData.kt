package io.github.aquabtww.utils

import io.github.aquabtww.Chambers
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

private val instance = Chambers.getInstance()

fun ItemStack.setPersistentData(key: String, value: String) {
    if (this.itemMeta == null) return
    editMeta { it.persistentDataContainer.set(NamespacedKey(instance, key), PersistentDataType.STRING, value) }
}

fun ItemStack.getPersistentData(key: String): String? {
    return itemMeta?.persistentDataContainer?.get(NamespacedKey(instance, key), PersistentDataType.STRING)
}