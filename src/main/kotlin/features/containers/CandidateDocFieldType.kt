package features.containers

enum class CandidateDocFieldType(val field: String) {
    CANDIDATE_DOC_FIELD_HEADER(field = "header"),
    CANDIDATE_DOC_FIELD_ENTITY(field = "entities"),
    CANDIDATE_DOC_FIELD_CONTENT(field = "content")
}