package edu.kit.ipd.eagle.impl.platforms.parse.specification.indirect;

import java.util.function.Supplier;

import edu.kit.ipd.eagle.impl.platforms.parse.prepipeline.PrePipelineMode;
import edu.kit.ipd.eagle.impl.platforms.parse.specification.AbstractAgentSpecification;
import edu.kit.ipd.parse.luna.agent.AbstractAgent;

/**
 * The base class for the agent specification in the INDIRECT context.
 *
 * @author Dominik Fuchss
 *
 * @param <A> the type of agent
 */
public abstract class IndirectAgentSpecification<A extends AbstractAgent> extends AbstractAgentSpecification<A> {
    /**
     * Create specification of an indirect agent based on one instance.
     *
     * @param agent the instance
     */
    protected IndirectAgentSpecification(Supplier<A> agent) {
        super(agent);
    }

    @Override
    public PrePipelineMode getMode() {
        return PrePipelineMode.INDIRECT;
    }

}
