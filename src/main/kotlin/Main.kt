import features.bindings.BagOfWordsBindings
import features.managers.AspectQuery


fun main() {
//    val loc = "/home/hcgs/data_science/top_100_examples.jsonl"
//    val mapper = jacksonObjectMapper()
//    val reader = File(loc).bufferedReader()
//    reader.forEachLine { line ->
//        val link: AspectLinkExample = mapper.readValue(line)
//        println(link.context.location)
//    }
//    reader.close()
//    val writer = getIndexWriter("blah")
//    writer.close()
//    val result = query.run(SimpBindings.SIMP_PARA_CONTENT_OVERLAP)

    val cborLoc = "/mnt/grapes/share/wiki2020/car-wiki2020-01-01/enwiki2020.cbor"
    val statLoc = "/home/jsc57/projects/eal-feature-generation/testing/corpus_stats.csv"
    val jsonLoc = "/mnt/grapes/share/entity_aspect_datasets/subsets/eal-v2.4/eal-v2.4-en-01-01-2020.test.jsonl"
//    val indexer = CorpusStatIndexer(indexFolderLoc = "testing", pageCborLoc = cborLoc)
//    indexer.run()

    val query = AspectQuery(jsonLoc = jsonLoc, statLoc = statLoc)
//    val result = query.run(BagOfWordsBindings.PARA_CONTENT_TO_ASPECT_CONTENT_BM25)
    val result = query.run(BagOfWordsBindings.PARA_CONTENT_TO_ASPECT_CONTENT_WORD2Vec)
    println(result)

}