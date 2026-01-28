package com.yash.edusmart.repository

import com.yash.edusmart.db.ChatDao
import com.yash.edusmart.db.ChatEntries
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface ChatLocalDbRepo
{
    suspend fun insert(data: ChatEntries)

    fun getMessages(): Flow<List<ChatEntries>>
}
class ChatLocalDbRepoImpl @Inject constructor(private val chatDao: ChatDao): ChatLocalDbRepo{
    override suspend fun insert(data: ChatEntries) {
        return chatDao.insertEntry(data)
    }

    override fun getMessages(): Flow<List<ChatEntries>> {
        return chatDao.getMessages()
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class ChatDbRepo(){
    @Binds
    @Singleton
    abstract fun bindChatDb(chatLocalDbRepoImpl: ChatLocalDbRepoImpl): ChatLocalDbRepo
}