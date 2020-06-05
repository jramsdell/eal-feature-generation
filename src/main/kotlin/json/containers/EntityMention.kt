package json.containers
import com.fasterxml.jackson.annotation.JsonProperty

data class EntityMention(
        @JsonProperty("entity_name") val entityName: String,
        @JsonProperty("entity_id") val entityId: String,
        @JsonProperty("mention") val mention: String,
        @JsonProperty("target_mention") val targetMention: Boolean,
        @JsonProperty("start") val start: Int,
        @JsonProperty("end") val end: Int
)