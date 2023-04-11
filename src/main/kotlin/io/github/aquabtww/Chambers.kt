package io.github.aquabtww

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import com.github.shynixn.mccoroutine.bukkit.setSuspendingExecutor
import com.mongodb.ConnectionString
import io.github.aquabtww.commands.ChambersCommand
import io.github.aquabtww.commands.ManageCommand
import io.github.aquabtww.commands.PartyCommand
import io.github.aquabtww.commands.StashCommand
import io.github.aquabtww.database.MongoDatabase
import io.github.aquabtww.database.managers.CPlayerManager
import io.github.aquabtww.database.repositories.CPlayerRepository
import io.github.aquabtww.items.CItemManager
import io.github.aquabtww.listeners.ChatListener
import io.github.aquabtww.listeners.ConnectionListeners
import io.github.aquabtww.listeners.MythicMobListeners
import io.github.aquabtww.stash.StashManager
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.litote.kmongo.coroutine.CoroutineDatabase

class Chambers : SuspendingJavaPlugin() {

    private val DATABASE_NAME = "b5uqyfi9ux4awyr"
    private val CONNECTION_STRING = ConnectionString("mongodb://ugltgog9wmmexvtummvd:qogsDvkRmcdpuaWFcCOM@n1-c2-mongodb-clevercloud-customers.services.clever-cloud.com:27017,n2-c2-mongodb-clevercloud-customers.services.clever-cloud.com:27017/b5uqyfi9ux4awyr?replicaSet=rs0")
    private lateinit var database: CoroutineDatabase

    private lateinit var playerRepository: CPlayerRepository
    private lateinit var playerManager: CPlayerManager

    private lateinit var itemManager: CItemManager

    private lateinit var stashManager: StashManager

    override suspend fun onLoadAsync() {
        database = MongoDatabase.create(DATABASE_NAME, CONNECTION_STRING)
    }

    override suspend fun onEnableAsync() {
        mm = MiniMessage.miniMessage()

        playerRepository = CPlayerRepository(database)
        playerManager = CPlayerManager(playerRepository)

        itemManager = CItemManager(dataFolder)

        stashManager = StashManager()

        registerListeners()
        registerCommands()

        playerManager.scheduleDatabaseUpdate()
    }

    private fun registerListeners() {
        setOf(
            ConnectionListeners(playerRepository, playerManager),
            MythicMobListeners(playerManager, itemManager, stashManager),
            ChatListener()
        ).forEach { Bukkit.getPluginManager().registerSuspendingEvents(it, this) }
    }

    private fun registerCommands() {
        setOf(
            ManageCommand(playerRepository, playerManager),
            PartyCommand(playerManager),
            ChambersCommand(itemManager),
            StashCommand(stashManager)
        ).forEach { getCommand(it.name)?.setSuspendingExecutor(it) }
    }


    companion object {
        /**
         * Singleton instance should not be used outside of
         * utilities that require the main instance of our plugin.
         *
         * Please do not randomly access data with this instance.
         */
        fun getInstance(): Chambers {
            return getPlugin(Chambers::class.java)
        }
    }

}

lateinit var mm: MiniMessage