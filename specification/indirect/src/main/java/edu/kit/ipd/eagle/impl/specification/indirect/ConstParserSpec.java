package edu.kit.ipd.eagle.impl.specification.indirect;

import java.util.List;

import edu.kit.ipd.eagle.impl.platforms.parse.PARSEInformationId;
import edu.kit.ipd.eagle.impl.platforms.parse.specification.indirect.IndirectAgentSpecification;
import edu.kit.ipd.indirect.constparser.ConstParser;

/**
 * Defines the agent specification for the {@link ConstParser}.
 *
 * @author Dominik Fuchss
 *
 */
public class ConstParserSpec extends IndirectAgentSpecification<ConstParser> {
    /**
     * Create the specification.
     */
    public ConstParserSpec() {
        super(ConstParser::new);
    }

    @Override
    public List<PARSEInformationId> getProvideIds() {
        return List.of(PARSEInformationId.CONSTITUENCY);
    }

    @Override
    public List<PARSEInformationId> getRequiresIds() {
        return List.of();
    }
}
