package edu.kit.ipd.are.agentanalysis.impl.parse.specification.parse;

import java.util.List;

import edu.kit.ipd.are.agentanalysis.impl.parse.PARSEInformationId;
import edu.kit.ipd.parse.wikiWSD.WordSenseDisambiguation;

/**
 * Defines the agent specification for the {@link WordSenseDisambiguation
 * WikiWSD}.
 *
 * @author Dominik Fuchss
 *
 */
public class WikiWSDSpec extends ParseAgentSpecification<WordSenseDisambiguation> {
	/**
	 * Create the specification.
	 */
	public WikiWSDSpec() {
		super(new WordSenseDisambiguation());
	}

	@Override
	public List<PARSEInformationId> getProvideIds() {
		return List.of(PARSEInformationId.WIKI_WSD);
	}

	@Override
	public List<PARSEInformationId> getRequiresIds() {
		return List.of();
	}
}