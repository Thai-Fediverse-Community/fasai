package com.thaifediverse.fasai.di

import com.google.gson.Gson
import com.thaifediverse.fasai.db.AccountManager
import com.thaifediverse.fasai.db.AppDatabase
import com.thaifediverse.fasai.network.MastodonApi
import com.thaifediverse.fasai.repository.TimelineRepository
import com.thaifediverse.fasai.repository.TimelineRepositoryImpl
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {
    @Provides
    fun providesTimelineRepository(
            db: AppDatabase,
            mastodonApi: MastodonApi,
            accountManager: AccountManager,
            gson: Gson
    ): TimelineRepository {
        return TimelineRepositoryImpl(db.timelineDao(), mastodonApi, accountManager, gson)
    }
}