package edu.kit.ipd.are.agentanalysis.impl.parse.specification.hypothesis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import edu.kit.ipd.are.agentanalysis.impl.parse.PARSEAgent;
import edu.kit.ipd.are.agentanalysis.impl.parse.PARSEGraphWrapper;
import edu.kit.ipd.are.agentanalysis.impl.parse.specification.parse.TopicExtractionSpec;
import edu.kit.ipd.are.agentanalysis.port.hypothesis.BasicHypothesesSet;
import edu.kit.ipd.are.agentanalysis.port.hypothesis.HypothesisRange;
import edu.kit.ipd.are.agentanalysis.port.hypothesis.IAgentHypothesisSpecification;
import edu.kit.ipd.are.agentanalysis.port.hypothesis.IHypothesesSelection;
import edu.kit.ipd.are.agentanalysis.port.hypothesis.IHypothesesSet;
import edu.kit.ipd.are.agentanalysis.port.hypothesis.IHypothesis;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;
import edu.kit.ipd.parse.luna.graph.INodeType;
import edu.kit.ipd.parse.luna.tools.ConfigManager;
import edu.kit.ipd.parse.topicExtraction.Topic;
import edu.kit.ipd.parse.topicExtraction.TopicExtraction;

/**
 * Defines the agent specification for the {@link TopicExtraction}. This is the
 * hypotheses realization for {@link TopicExtractionSpec}.
 *
 * @author Dominik Fuchss
 *
 */
public class TopicExtractionHypothesisSpec extends TopicExtractionSpec implements IAgentHypothesisSpecification<PARSEAgent, PARSEGraphWrapper> {

	private static final String TOPIC_ATTRIBUTE = "topic";
	private static final String TOPICS_NODE_TYPE = "topics";

	/**
	 * Create the specification by using the default amount of hypotheses.
	 */
	public TopicExtractionHypothesisSpec() {
		this(IAgentHypothesisSpecification.DEFAULT_HYPOTHESES);
	}

	/**
	 * Create the specification by using a specific amount of hypotheses.
	 *
	 * @param maxHypotheses the specific maximum of generated hypotheses per
	 *                      {@link IHypothesesSet}
	 */
	public TopicExtractionHypothesisSpec(int maxHypotheses) {
		super();
		// Set Max Hypotheses in Agent's configuration.
		Properties props = ConfigManager.getConfiguration(TopicExtraction.class);
		props.setProperty("TOPICS", String.valueOf(Math.max(maxHypotheses, -1)));
	}

	@Override
	public List<IHypothesesSet> getHypothesesForNonHypothesesExecution(PARSEGraphWrapper data) {
		IGraph graph = data.getGraph();
		List<Topic> topics = TopicExtraction.getTopicsFromIGraph(graph);

		List<TopicHypothesis> hyps = new ArrayList<>();
		for (Topic t : topics) {
			hyps.add(new TopicHypothesis(t));
		}
		return Arrays.asList(new BasicHypothesesSet("Topic Hypotheses", HypothesisRange.SENTENCE, hyps, false));
	}

	@Override
	public List<IHypothesesSet> getHypothesesFromDataStructure(PARSEGraphWrapper data) {
		IGraph graph = data.getGraph();
		List<INode> nodes = graph.getNodesOfType(graph.getNodeType(TopicExtractionHypothesisSpec.TOPICS_NODE_TYPE));
		if (nodes.size() != 1) {
			return new ArrayList<>();
		}

		List<Topic> topics = TopicExtraction.getTopicsFromIGraph(graph);
		topics.sort((t1, t2) -> -Double.compare(t1.getScore(), t2.getScore()));

		List<TopicHypothesis> hyps = new ArrayList<>();
		for (Topic t : topics) {
			hyps.add(new TopicHypothesis(t));
		}
		if (hyps.isEmpty()) {
			return List.of();
		}
		return Arrays.asList(new BasicHypothesesSet("Topic Hypotheses", HypothesisRange.SENTENCE, hyps, false));
	}

	@Override
	public void applyHypothesesToDataStructure(PARSEGraphWrapper data, List<IHypothesesSelection> hypotheses) {
		IGraph graph = data.getGraph();
		this.checkSelection(hypotheses);
		if (hypotheses.size() > 1) {
			throw new IllegalArgumentException("Too many HypothesesGroups are selected ..");
		}

		INodeType tokenType;
		if (graph.hasNodeType(TopicExtractionHypothesisSpec.TOPICS_NODE_TYPE)) {
			tokenType = graph.getNodeType(TopicExtractionHypothesisSpec.TOPICS_NODE_TYPE);
		} else {
			tokenType = graph.createNodeType(TopicExtractionHypothesisSpec.TOPICS_NODE_TYPE);
		}
		if (!tokenType.containsAttribute(TopicExtractionHypothesisSpec.TOPIC_ATTRIBUTE, "java.util.List")) {
			tokenType.addAttributeToType("java.util.List", TopicExtractionHypothesisSpec.TOPIC_ATTRIBUTE);
		}

		List<IHypothesis> hyps = hypotheses.get(0).getSelectedHypotheses();
		List<Topic> topics = hyps.stream().map(h -> ((TopicHypothesis) h).topic).collect(Collectors.toList());

		List<INode> nodes = graph.getNodesOfType(graph.getNodeType(TopicExtractionHypothesisSpec.TOPICS_NODE_TYPE));
		INode node;
		if (nodes.isEmpty()) {
			node = graph.createNode(graph.getNodeType(TopicExtractionHypothesisSpec.TOPICS_NODE_TYPE));
		} else {
			node = nodes.get(0);
		}
		node.setAttributeValue(TopicExtractionHypothesisSpec.TOPIC_ATTRIBUTE, topics);
	}

	private void checkSelection(List<IHypothesesSelection> selection) {
		for (IHypothesesSelection s : selection) {
			if (!(s.getAllHypotheses() instanceof BasicHypothesesSet)) {
				throw new IllegalArgumentException("Not a valid HypothesesGroup: " + s.getAllHypotheses());
			}
			for (IHypothesis h : s.getSelectedHypotheses()) {
				if (!(h instanceof TopicHypothesis)) {
					throw new IllegalArgumentException("Not a valid TopicHypothesis: " + h);
				}
			}
		}
	}

	private static final class TopicHypothesis implements IHypothesis {

		private static final long serialVersionUID = -6704832506392171553L;

		private Topic topic;

		// For deserialize
		private TopicHypothesis() {
		}

		private TopicHypothesis(Topic topic) {
			this.topic = topic;
		}

		@Override
		public String getPrettyInformation() {
			return this.topic.getLabel();
		}

		@Override
		public double getConfidence() {
			return this.topic.getScore();
		}

		@Override
		public String toString() {
			return "Hypothesis [topic=" + this.topic + ", score=" + this.topic.getScore() + "]";
		}

		@Override
		public String getValue() {
			return this.topic.getLabel();
		}
	}

	@Override
	public HypothesisRange getHypothesesRange() {
		return HypothesisRange.SENTENCE;
	}
}
