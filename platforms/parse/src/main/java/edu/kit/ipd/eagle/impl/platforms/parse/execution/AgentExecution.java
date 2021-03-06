package edu.kit.ipd.eagle.impl.platforms.parse.execution;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.kit.ipd.eagle.impl.platforms.parse.GraphUtils;
import edu.kit.ipd.eagle.impl.platforms.parse.PARSEAgent;
import edu.kit.ipd.eagle.impl.platforms.parse.PARSEAgentHelper;
import edu.kit.ipd.eagle.impl.platforms.parse.PARSEGraphWrapper;
import edu.kit.ipd.eagle.impl.platforms.parse.prepipeline.PrePipelineMode;
import edu.kit.ipd.eagle.impl.platforms.parse.specification.AbstractAgentSpecification;
import edu.kit.ipd.eagle.port.AgentHelper;
import edu.kit.ipd.eagle.port.IAgentExecution;
import edu.kit.ipd.parse.luna.agent.AbstractAgent;

/**
 * Defines a basic realization of an {@link IAgentExecution}.
 *
 * @author Dominik Fuchss
 *
 */
public class AgentExecution implements IAgentExecution<PARSEAgent, PARSEGraphWrapper, AbstractAgentSpecification<? extends AbstractAgent>> {

    private Set<AbstractAgentSpecification<? extends AbstractAgent>> agents = new HashSet<>();

    @Override
    public void loadAgent(AbstractAgentSpecification<? extends AbstractAgent> agentSpec) {
        this.agents.add(agentSpec);
    }

    @Override
    public void unloadAgents() {
        this.agents.clear();
    }

    @Override
    public PARSEGraphWrapper execute(PARSEGraphWrapper inputGraph) {

        PrePipelineMode ppm = inputGraph.getPrePipelineMode();

        // Check PPM
        List<AbstractAgentSpecification<? extends AbstractAgent>> invalidAgents = PARSEAgentHelper.findInvalidAgents(this.agents, ppm);
        if (!invalidAgents.isEmpty()) {
            if (IAgentExecution.logger.isErrorEnabled()) {
                IAgentExecution.logger.error("Agent(s) " + invalidAgents + " are no valid agents for PrePipeline " + ppm);
            }
            return null;
        }

        // Execute Agents ..
        List<AbstractAgentSpecification<? extends AbstractAgent>> specsToRun = AgentHelper.findAgentOrder(this.agents);
        if (specsToRun == null) {
            return null;
        }

        PARSEGraphWrapper graph = inputGraph;

        for (AbstractAgentSpecification<? extends AbstractAgent> next : specsToRun) {
            if (IAgentExecution.logger.isDebugEnabled()) {
                IAgentExecution.logger.debug("Executing " + next);
            }

            var nextGraph = next.createAgentInstance().execute(graph);
            if (nextGraph == null) {
                if (IAgentExecution.logger.isErrorEnabled()) {
                    IAgentExecution.logger.error("Failed to execute " + next);
                }
                return null;
            }
            graph = nextGraph;

            if (IAgentExecution.logger.isDebugEnabled()) {
                IAgentExecution.logger.debug("After " + next.createAgentInstance().getClass().getSimpleName() + " " + GraphUtils.getStats(graph.getGraph()));
            }
        }

        return graph;
    }

}
