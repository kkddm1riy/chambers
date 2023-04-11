package io.github.aquabtww.database

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import org.bson.UuidRepresentation
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.serialization.SerializationClassMappingTypeService

object MongoDatabase {

    fun create(
        dbName: String,
        connectionStr: ConnectionString
    ): CoroutineDatabase {

        System.setProperty(
            "org.litote.mongo.mapping.service",
            SerializationClassMappingTypeService::class.qualifiedName!!
        )

        return KMongo.createClient(
            MongoClientSettings.builder()
                .applyConnectionString(connectionStr)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .build()
        ).coroutine.getDatabase(dbName)
    }

}