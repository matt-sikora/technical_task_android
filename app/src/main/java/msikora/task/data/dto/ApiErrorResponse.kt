package msikora.task.data.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer

@Serializable
data class ApiErrorResponse(
    @Serializable(with = ApiErrorDtoListSerializer::class)
    val `data`: List<ErrorDto>?,
    val meta: MetadataDto?,
)

object ApiErrorDtoListSerializer
    : JsonTransformingSerializer<List<ErrorDto>>(ListSerializer(ErrorDto.serializer())) {
    // If response is not an array, then it is a single object that should be wrapped into the array
    override fun transformDeserialize(element: JsonElement): JsonElement =
        if (element !is JsonArray) JsonArray(listOf(element)) else element
}
