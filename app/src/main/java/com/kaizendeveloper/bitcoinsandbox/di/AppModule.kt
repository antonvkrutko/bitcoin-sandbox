package com.kaizendeveloper.bitcoinsandbox.di

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import com.kaizendeveloper.bitcoinsandbox.db.SandboxDatabase
import com.kaizendeveloper.bitcoinsandbox.db.repository.MempoolRepository
import com.kaizendeveloper.bitcoinsandbox.db.repository.UTXOPoolRepository
import com.kaizendeveloper.bitcoinsandbox.transaction.Mempool
import com.kaizendeveloper.bitcoinsandbox.transaction.UTXOPool
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application
    }

    @Provides
    @Singleton
    fun provideDatabase(context: Context): SandboxDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            SandboxDatabase::class.java,
            SandboxDatabase.DATABASE_NAME
        ).allowMainThreadQueries().build()
    }

    @Provides
    @Singleton
    fun provideUserDao(db: SandboxDatabase) = db.userDao()

    @Provides
    @Singleton
    fun provideUTXOPoolDao(db: SandboxDatabase) = db.utxoPoolDao()

    @Provides
    @Singleton
    fun provideMempoolDao(db: SandboxDatabase) = db.mempoolDao()

    @Provides
    @Singleton
    fun provideMempool(mempoolRepository: MempoolRepository) = Mempool(mempoolRepository)

    @Provides
    @Singleton
    fun provideUtxoPool(utxoPoolRepository: UTXOPoolRepository) = UTXOPool(utxoPoolRepository)
}
