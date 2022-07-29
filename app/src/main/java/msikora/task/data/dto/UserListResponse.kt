package msikora.task.data.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer

@Serializable
data class UserListResponse(
    @Serializable(with = UserListResponseSerializer::class)
    val `data`: List<UserDto>?,
    val meta: MetadataDto?
)

object UserListResponseSerializer
    : JsonTransformingSerializer<List<UserDto>>(ListSerializer(UserDto.serializer())) {
    // If response is not an array, then it is a single object that should be wrapped into the array
    override fun transformDeserialize(element: JsonElement): JsonElement =
        if (element !is JsonArray) JsonArray(listOf(element)) else element
}