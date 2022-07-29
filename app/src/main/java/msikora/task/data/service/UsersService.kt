package msikora.task.data.service

import com.haroldadmin.cnradapter.NetworkResponse
import msikora.task.data.dto.ApiErrorResponse
import msikora.task.data.dto.NewUserDto
import msikora.task.data.dto.UserListResponse
import retrofit2.http.*

interface UsersService {

    @GET("public/v1/users")
    suspend fun getUsers(@Query("page") page: Int? = null): NetworkResponse<UserListResponse, ApiErrorResponse>

    @POST("public/v1/users")
    suspend fun createUser(@Body user: NewUserDto): NetworkResponse<UserListResponse, ApiErrorResponse>

    @DELETE("public/v1/users/{userId}")
    suspend fun deleteUser(@Path("userId") userId: Int): NetworkResponse<Unit, ApiErrorResponse>
}
