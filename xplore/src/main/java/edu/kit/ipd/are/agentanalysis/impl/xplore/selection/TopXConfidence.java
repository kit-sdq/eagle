package edu.kit.ipd.are.agentanalysis.impl.xplore.selection;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ipd.are.agentanalysis.port.hypothesis.HypothesisRange;
import edu.kit.ipd.are.agentanalysis.port.hypothesis.IHypothesesSelection;
import edu.kit.ipd.are.agentanalysis.port.hypothesis.IHypothesesSet;
import edu.kit.ipd.are.agentanalysis.port.xplore.selection.ISelectionProvider;

/**
 * A simple selection provider for {@link HypothesisRange#NODE}. The provider
 * generates selections by using the [Top1 for all hypotheses sets], [Top2 for
 * all hypotheses sets], ...
 *
 * @author Dominik Fuchss
 *
 */
public class TopXConfidence implements ISelectionProvider {

	private int maxXRanking;

	/**
	 * Create the selection provider.
	 *
	 * @param maxXRanking the maximum amount of selections generated by this
	 *                    provider
	 */
	public TopXConfidence(int maxXRanking) {
		this.maxXRanking = Math.max(1, maxXRanking);
	}

	@Override
	public List<List<IHypothesesSelection>> findSelection(List<IHypothesesSet> hypotheses) {
		List<List<IHypothesesSelection>> result = new ArrayList<>();

		for (int topX = 0; topX < this.maxXRanking; topX++) {
			List<IHypothesesSelection> topXSelection = new ArrayList<>();
			for (IHypothesesSet group : hypotheses) {
				if (group.getHypotheses().size() <= topX) {
					return result;
				}
				topXSelection.add(new HypothesesSelection(group, List.of(group.getHypotheses().get(topX))));
			}
			if (topXSelection.isEmpty()) {
				return result;
			}
			result.add(topXSelection);
		}

		return result;
	}

}
