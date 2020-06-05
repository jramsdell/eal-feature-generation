package json.containers
import com.fasterxml.jackson.annotation.JsonProperty

data class AspectLinkExample(
        @JsonProperty("unhashed_id") val unhashedId: String,
        @JsonProperty("id") val id: String,
        @JsonProperty("context") val context: Context,
        @JsonProperty("true_aspect") val trueAspect: String,
        @JsonProperty("candidate_aspects") val candidateAspects: List<Aspect>
)