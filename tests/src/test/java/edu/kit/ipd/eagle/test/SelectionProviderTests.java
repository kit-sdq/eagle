package edu.kit.ipd.eagle.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.kit.ipd.eagle.impl.xplore.selection.FullExploration;
import edu.kit.ipd.eagle.impl.xplore.selection.RandomHypothesis;
import edu.kit.ipd.eagle.port.hypothesis.HypothesisRange;
import edu.kit.ipd.eagle.port.hypothesis.IHypothesesSelection;
import edu.kit.ipd.eagle.port.xplore.dto.HypothesesSetDTO;
import edu.kit.ipd.eagle.port.xplore.dto.HypothesisDTO;
import edu.kit.ipd.eagle.port.xplore.selection.ISelectionProvider;

/**
 * Some tests for {@link ISelectionProvider Selection Providers}
 *
 * @author Dominik Fuchss
 *
 */
public class SelectionProviderTests {
	private HypothesisDTO hyp1;
	private HypothesisDTO hyp2;
	private HypothesesSetDTO set1;
	private HypothesesSetDTO set2;

	/**
	 * Create some hypotheses for testing.
	 */
	@Before
	public void createSomeHypotheses() {
		this.hyp1 = new HypothesisDTO();
		this.hyp1.setConfidence(1);
		this.hyp1.setValue("val1");

		this.hyp2 = new HypothesisDTO();
		this.hyp2.setConfidence(2);
		this.hyp2.setValue("val2");

		this.set1 = new HypothesesSetDTO();
		this.set1.setHypotheses(List.of(this.hyp1));
		this.set1.setHypothesesRange(HypothesisRange.ELEMENT);
		this.set1.setOnlyOneHypothesisValid(true);

		this.set2 = new HypothesesSetDTO();
		this.set2.setHypotheses(List.of(this.hyp1, this.hyp2));
		this.set2.setHypothesesRange(HypothesisRange.ELEMENT);
		this.set2.setOnlyOneHypothesisValid(true);
	}

	/**
	 * Check selection mechanisms of {@link FullExploration}.
	 */
	@Test
	public void checkAllCombinationsIfOnlyOneValid() {
		FullExploration acioov = new FullExploration();
		var selections = acioov.findSelection(List.of(this.set1, this.set2));
		this.checkSelectionValid(selections);
		Assert.assertEquals(2, selections.size());

		selections = acioov.findSelection(List.of(this.set2, this.set2));
		this.checkSelectionValid(selections);
		Assert.assertEquals(4, selections.size());

		selections = acioov.findSelection(List.of(this.set2, this.set2, this.set2));
		this.checkSelectionValid(selections);
		Assert.assertEquals(8, selections.size());
	}

	/**
	 * Check selection mechanisms of {@link RandomHypothesis}.
	 */
	@Test
	public void checkRandomSelectionIfOnlyOneValid() {
		RandomHypothesis rsioov = new RandomHypothesis(-1, false); // Default full exploration ..
		var selections = rsioov.findSelection(List.of(this.set2, this.set2, this.set2));
		this.checkSelectionValid(selections);
		Assert.assertEquals(8, selections.size());

		rsioov = new RandomHypothesis(0.5, false); // 50 % of paths
		selections = rsioov.findSelection(List.of(this.set2, this.set2, this.set2));
		this.checkSelectionValid(selections);
		Assert.assertEquals(4, selections.size());

		rsioov = new RandomHypothesis(3, false); // 3 max
		selections = rsioov.findSelection(List.of(this.set2, this.set2, this.set2));
		this.checkSelectionValid(selections);
		Assert.assertEquals(3, selections.size());
	}

	private void checkSelectionValid(List<List<IHypothesesSelection>> selections) {
		for (var selection : selections) {
			for (var selected : selection) {
				Assert.assertTrue(List.of(this.set1, this.set2).contains(selected.getAllHypotheses()));
				Assert.assertTrue(selected.getSelectedHypotheses().stream().allMatch(h -> List.of(this.hyp1, this.hyp2).contains(h)));
			}
		}

	}

}
