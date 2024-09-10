package com.mahikr.keepup.common.di.db

import android.util.Log
import com.mahikr.keepup.data.db.ITaskImpl
import com.mahikr.keepup.data.db.model.Task
import com.mahikr.keepup.domain.db.ITask
import com.mahikr.keepup.domain.db.usecase.GetTaskById
import com.mahikr.keepup.domain.db.usecase.GetTasks
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.InitialDataCallback
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy

@InstallIn(SingletonComponent::class)
@Module
object DbModule {

    @Provides
    fun provideRealmDb(): Realm {
        val configuration =
            RealmConfiguration.Builder(schema = setOf(Task::class))
                .compactOnLaunch() //will allow optimize the size of the DB
                .initialData(callback = InitialDataCallback {
                    //pre-populate db
                    Log.d("_TAG", "provideRealm: ${ITaskImpl.prePopulatedHabits.size}")
                    ITaskImpl.prePopulatedHabits.forEach {
                        Log.d("_TAG", "provideRealm:forEach $it")
                        this.copyToRealm(it, updatePolicy = UpdatePolicy.ALL)
                    }
                }).build()
        return Realm.open(configuration = configuration)
    }

    @Provides
    fun provideITask(realm: Realm): ITask = ITaskImpl(realm = realm)

    @Provides
    fun provideGetTasks(iTask: ITask) = GetTasks(iTask = iTask)

    @Provides
    fun provideGetTaskById(iTask: ITask) = GetTaskById(iTask = iTask)

}