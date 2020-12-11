package edu.kit.ipd.are.agentanalysis.impl.parse.specification.indirect;

import java.util.List;

import edu.kit.ipd.are.agentanalysis.impl.parse.PARSEInformationId;
import edu.kit.ipd.indirect.depparser.DepParser;
import edu.kit.ipd.parse.luna.tools.ConfigManager;

/**
 * Defines the agent specification for the {@link DepParser}.
 *
 * @author Dominik Fuchss
 *
 */
public class DepParserSpec extends IndirectAgentSpecification<DepParser> {
	/**
	 * Create the specification.
	 */
	public DepParserSpec() {
		super(new DepParser());
		var props = ConfigManager.getConfiguration(DepParser.class);
		props.setProperty("depparse.model", "edu/stanford/nlp/models/parser/nndep/english_UD.gz");
	}

	@Override
	public List<PARSEInformationId> getProvideIds() {
		return List.of(PARSEInformationId.DEPENDENCIES);
	}

	@Override
	public List<PARSEInformationId> getRequiresIds() {
		return List.of();
	}
}