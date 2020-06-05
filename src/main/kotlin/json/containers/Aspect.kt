package json.containers
import com.fasterxml.jackson.annotation.JsonProperty

data class Aspect(
        @JsonProperty("aspect_id") val aspectId: String,
        @JsonProperty("aspect_name") val aspectName: String,
        @JsonProperty("location") val location: Location,
        @JsonProperty("aspect_content") val aspectContent: AnnotatedText
)