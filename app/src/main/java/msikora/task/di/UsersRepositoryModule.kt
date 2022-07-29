package msikora.task.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import msikora.task.data.UsersRepository
import msikora.task.data.UsersRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class UsersRepositoryModule {

    @Binds
    abstract fun bind(
        impl: UsersRepositoryImpl
    ): UsersRepository
}