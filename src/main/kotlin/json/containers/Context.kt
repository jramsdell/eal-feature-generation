package json.containers
import com.fasterxml.jackson.annotation.JsonProperty

data class Context(
        @JsonProperty("target_entity") val targetEntity: String,
        @JsonProperty("location") val location: Location,
        @JsonProperty("sentence") val sentence: AnnotatedText,
        @JsonProperty("paragraph") val paragraph: AnnotatedText
)