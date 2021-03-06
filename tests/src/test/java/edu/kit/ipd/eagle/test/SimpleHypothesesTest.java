package edu.kit.ipd.eagle.test;

import java.io.IOException;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import edu.kit.ipd.eagle.impl.platforms.parse.PARSEGraphWrapper;
import edu.kit.ipd.eagle.impl.platforms.parse.execution.AgentExecution;
import edu.kit.ipd.eagle.impl.platforms.parse.prepipeline.IPrePipeline;
import edu.kit.ipd.eagle.impl.platforms.parse.prepipeline.PARSEPrePipeline;
import edu.kit.ipd.eagle.impl.specification.parse.hypothesis.WikiWSDHypothesisSpec;
import edu.kit.ipd.indirect.constparser.ConstParser;
import edu.kit.ipd.indirect.depparser.DepParser;
import edu.kit.ipd.indirect.textSNLP.Stanford;
import edu.kit.ipd.indirect.textner.TextNERTagger;
import edu.kit.ipd.parse.luna.tools.ConfigManager;

/**
 * Some simple tests to ensure that the agent execution and agent exploration are operational.
 *
 * @author Dominik Fuchss
 *
 */
public class SimpleHypothesesTest extends TestBase {
    private AgentExecution analysis;
    private PARSEGraphWrapper graph;

    /**
     * Configure PARSE & INDIRECT.
     */
    @BeforeClass
    public static void setupClass() {
        Properties props = ConfigManager.getConfiguration(Stanford.class);
        props.setProperty("TAGGER_MODEL", "/edu/stanford/nlp/models/pos-tagger/english-bidirectional/english-bidirectional-distsim.tagger");
        props = ConfigManager.getConfiguration(ConstParser.class);
        props.setProperty("parse.buildgraphs", "false");
        props.setProperty("ner.applyNumericClassifiers", "false");
        props.setProperty("ner.useSUTime", "false");
        props = ConfigManager.getConfiguration(DepParser.class);
        props.setProperty("depparse.model", "edu/stanford/nlp/models/parser/nndep/english_UD.gz");
        props.setProperty("DEPENDENCY_ANNOTATION_TYPE", "EnhancedPlusPlusDependenciesAnnotation");
        props = ConfigManager.getConfiguration(TextNERTagger.class);
        props.setProperty("ner.applyNumericClassifiers", "true");
        props.setProperty("ner.useSUTime", "false");
        props.setProperty("ner.applyFineGrained", "false");
    }

    /**
     * Create new graph by some sentence.
     *
     * @throws IOException iff texts cannot loaded
     */
    @Before
    public void setup() throws IOException {
        TestBase.skipIffCI();
        this.analysis = new AgentExecution();
        this.graph = this.getNewGraph(new PARSEPrePipeline());
    }

    private PARSEGraphWrapper getNewGraph(IPrePipeline ppl) throws IOException {
        // var texts = TestBase.getTexts();
        // var text = texts.get(texts.firstKey());
        var text = "John, go to the fridge next to the cupboard.";
        return ppl.createGraph(text);
    }

    /**
     * Check whether the {@link WikiWSDHypothesisSpec} generates hypotheses.
     */
    @Test
    @Ignore("ProNat is still broken")
    public void testHypothesisOfWikiWSD() {
        WikiWSDHypothesisSpec wwhs = new WikiWSDHypothesisSpec();
        this.analysis.loadAgent(wwhs);

        PARSEGraphWrapper wsdAnalyzed = this.analysis.execute(this.graph);
        Assert.assertNotNull(wsdAnalyzed);
        Assert.assertNotSame(this.graph, wsdAnalyzed);

        var hyps = wwhs.getHypothesesFromDataStructure(wsdAnalyzed);
        Assert.assertNotNull(hyps);
        Assert.assertFalse(hyps.isEmpty());
    }

    // Disabled since Topic Extraction (Upstream) has a Bug

    // /**
    // * Check whether the {@link TopicExtractionHypothesisSpec} generates hypotheses. based on the
    // * {@link WikiWSDHypothesisSpec}.
    // */
    // @Test
    // @Ignore("TD is broken")
    // public void testHypothesisOfTopicExtractor() {
    // WikiWSDHypothesisSpec wwhs = new WikiWSDHypothesisSpec();
    // TopicExtractionHypothesisSpec tehs = new TopicExtractionHypothesisSpec();
    // this.analysis.loadAgents(List.of(wwhs, tehs));
    //
    // PARSEGraphWrapper topicAnalyzed = this.analysis.execute(this.graph);
    // Assert.assertNotNull(topicAnalyzed);
    // Assert.assertNotSame(this.graph, topicAnalyzed);
    //
    // var hyps = tehs.getHypothesesFromDataStructure(topicAnalyzed);
    // Assert.assertNotNull(hyps);
    // Assert.assertFalse(hyps.isEmpty());
    // }

    // /**
    // * Check whether the serialisation of {@link IExplorationResult IExplorationResults} of {@link SimpleExploration}
    // is
    // * operational.
    // *
    // * @throws Exception iff something went wrong
    // */
    // @Test
    // @Ignore("TD is broken")
    // public void testHypothesisCombinationSimpleExploration() throws Exception {
    // WikiWSDHypothesisSpec wwhs = new WikiWSDHypothesisSpec();
    // TopicExtractionHypothesisSpec tehs = new TopicExtractionHypothesisSpec();
    // SimpleExploration<PARSEAgent, PARSEGraphWrapper> explorer = new SimpleExploration<>(this.graph,
    // this.graph.getText(), 3);
    //
    // explorer.loadHypothesisAgent(wwhs);
    // explorer.loadHypothesisAgent(tehs);
    //
    // var result = explorer.explore();
    // var jsonGetter = Serialize.getObjectMapperForGetters(true).writeValueAsString(result);
    // var jsonInherit = Serialize.getObjectMapperForInheritance(true).writeValueAsString(result);
    //
    // Assert.assertTrue(jsonGetter.contains("\"explorationRoot\""));
    // Assert.assertFalse(jsonInherit.contains("\"explorationRoot\""));
    //
    // Assert.assertFalse(jsonGetter.contains("\"startNode\""));
    // Assert.assertTrue(jsonInherit.contains("\"startNode\""));
    //
    // FileWriter fw = new FileWriter(this.getTargetFile("output.json"));
    // fw.write(jsonGetter);
    // fw.close();
    // }

    // /**
    // * Check whether the serialisation of {@link IExplorationResult IExplorationResults} of {@link
    // SpecificExploration}
    // * is operational.
    // *
    // * @throws Exception iff something went wrong
    // */
    // @Test
    // @Ignore("TD is broken")
    // public void testHypothesisCombinationSpecificExploration() throws Exception {
    // WikiWSDHypothesisSpec wwhs = new WikiWSDHypothesisSpec();
    // TopicExtractionHypothesisSpec tehs = new TopicExtractionHypothesisSpec();
    //
    // SpecificExploration<PARSEAgent, PARSEGraphWrapper> explorer = new SpecificExploration<>(this.graph,
    // this.graph.getText(), 3);
    // explorer.loadHypothesisAgent(wwhs, new SameWordSameDecision(new TopXConfidence(3)));
    // explorer.loadHypothesisAgent(tehs);
    //
    // var result = explorer.explore();
    // var jsonGetter = Serialize.getObjectMapperForGetters(true).writeValueAsString(result);
    // var jsonInherit = Serialize.getObjectMapperForInheritance(true).writeValueAsString(result);
    //
    // Assert.assertTrue(jsonGetter.contains("\"explorationRoot\""));
    // Assert.assertFalse(jsonInherit.contains("\"explorationRoot\""));
    //
    // Assert.assertFalse(jsonGetter.contains("\"startNode\""));
    // Assert.assertTrue(jsonInherit.contains("\"startNode\""));
    //
    // FileWriter fw = new FileWriter(this.getTargetFile("output.json"));
    // fw.write(jsonGetter);
    // fw.close();
    // }

    // /**
    // * Check whether the path extraction of {@link LayeredExploration} is operational.
    // */
    // @Test
    // @Ignore("TD is broken")
    // public void testPathGeneration() {
    // WikiWSDHypothesisSpec wwhs = new WikiWSDHypothesisSpec();
    // TopicExtractionHypothesisSpec tehs = new TopicExtractionHypothesisSpec();
    // OntologySelectorHypothesisSpec oshs = new OntologySelectorHypothesisSpec(this.loadActorOntologies(),
    // this.loadEnvOntologies());
    // SimpleExploration<PARSEAgent, PARSEGraphWrapper> explorer = new SimpleExploration<>(this.graph,
    // this.graph.getText(), 3);
    //
    // explorer.loadHypothesisAgent(wwhs);
    // explorer.loadHypothesisAgent(tehs);
    // explorer.loadHypothesisAgent(oshs);
    //
    // var result = explorer.explore();
    // var paths = result.getPaths();
    // // No of paths should be 9 (split exploration at WSD & Topics)
    // Assert.assertEquals(3 * 3, paths.size());
    //
    // }
    //
    // /**
    // * Check whether the path extraction of {@link LayeredExploration} is operational, by using
    // * {@link SimpleExploration#loadAgent(org.fuchss.agentanalysis.port.IAgentSpecification)} instead of
    // * {@link
    // SimpleExploration#loadHypothesisAgent(org.fuchss.agentanalysis.port.hypothesis.IAgentHypothesisSpecification)}.
    // */
    // @Test
    // @Ignore("TD is broken")
    // public void testPathGenerationNoHypothesesSplit() {
    // WikiWSDSpec wwhs = new WikiWSDSpec();
    // TopicExtractionHypothesisSpec tehs = new TopicExtractionHypothesisSpec();
    // OntologySelectorHypothesisSpec oshs = new OntologySelectorHypothesisSpec(this.loadActorOntologies(),
    // this.loadEnvOntologies());
    // SimpleExploration<PARSEAgent, PARSEGraphWrapper> explorer = new SimpleExploration<>(this.graph,
    // this.graph.getText(), 3);
    //
    // explorer.loadAgent(wwhs);
    // explorer.loadHypothesisAgent(tehs);
    // explorer.loadHypothesisAgent(oshs);
    //
    // var result = explorer.explore();
    // var paths = result.getPaths();
    // // No of paths should be 3 (split exploration at Topics)
    // Assert.assertEquals(3, paths.size());
    //
    // }

}
