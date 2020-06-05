package json.containers
import com.fasterxml.jackson.annotation.JsonProperty

data class AnnotatedText(
        @JsonProperty("content") val content: String,
        @JsonProperty("entities") val entities: List<EntityMention>
)