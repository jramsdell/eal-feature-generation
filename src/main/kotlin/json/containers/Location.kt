package json.containers
import com.fasterxml.jackson.annotation.JsonProperty

data class Location(
        @JsonProperty("location_id") val locationId: String,
        @JsonProperty("page_id") val pageId: String,
        @JsonProperty("page_title") val pageTitle: String,
        @JsonProperty("paragraph_id") val paragraphId: String?,
        @JsonProperty("section_id") val sectionId: List<String>,
        @JsonProperty("section_headings") val sectionHeadings: List<String>
)