package io.github.aquabtww.items

import io.github.aquabtww.utils.jsonF
import kotlinx.serialization.decodeFromString
import org.bukkit.inventory.ItemStack
import java.io.File
import java.util.Collections

class CItemManager(
    private val dataFolder: File
) {

    private val _items = mutableSetOf<CItem>()

    val items: Set<CItem>
        get() = Collections.unmodifiableSet(_items)

    fun reload() {
        if (_items.isNotEmpty()) _items.clear()

        val dir = File(dataFolder.absolutePath)
        val file = File(dataFolder.absolutePath + "\\items.json")

        dir.mkdirs()
        if (!file.exists()) file.createNewFile()

        try {
            _items.addAll(jsonF.decodeFromString<Set<CItem>>(file.bufferedReader().readText()))
        } catch (e: Exception) { e.printStackTrace() }
    }

    fun findByID(id: String): ItemStack? {
        return items.firstOrNull { it.id == id }?.toItemStack()
    }

    init {
        reload()
    }

}