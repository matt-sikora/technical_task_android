@file:OptIn(ExperimentalSerializationApi::class)

package msikora.task.di

import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import msikora.task.BuildConfig
import msikora.task.data.Token
import msikora.task.data.service.UsersService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Provides
    @Singleton
    fun retrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://gorest.co.in/")
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Reusable
    fun loggingInterceptor() = if (BuildConfig.DEBUG) {
        null
    } else {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
    }

    @Provides
    @Singleton
    fun usersService(retrofit: Retrofit): UsersService = retrofit.create(UsersService::class.java)

    @Provides
    @Singleton
    fun token() = Token(BuildConfig.TOKEN)

    @Provides
    @Singleton
    fun okHttp(token: Token, loggingInterceptor: HttpLoggingInterceptor?) =
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val newRequest =
                    chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer ${token.asString}")
                        .build()
                chain.proceed(newRequest)
            }
            .apply {
                if (loggingInterceptor != null) {
                    addInterceptor(loggingInterceptor)
                }
            }
            .build()
}