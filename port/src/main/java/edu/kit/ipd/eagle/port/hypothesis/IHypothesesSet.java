package edu.kit.ipd.eagle.port.hypothesis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This interface defines a set of {@link IHypothesis Hypotheses}. <b>Annotate all your public getters that are not
 * defined here with {@link JsonIgnore}!</b>
 *
 * @author Dominik Fuchss
 *
 */
public interface IHypothesesSet extends Serializable {

	/**
	 * Get all hypotheses of the group ordered by {@link IHypothesis#getConfidence() confidence} descending.
	 *
	 * @return a sorted list of hypotheses
	 */
	@JsonIgnore
	default List<IHypothesis> getSortedHypotheses() {
		var res = new ArrayList<>(this.getHypotheses());
		res.sort((a, b) -> -Double.compare(a.getConfidence(), b.getConfidence()));
		return res;
	}

	/**
	 * Get all hypotheses of the group.
	 *
	 * @return a sorted list of hypotheses
	 */
	List<IHypothesis> getHypotheses();

	/**
	 * Indicates whether only one hypothesis of the group can be valid.
	 *
	 * @return the indicator for exclusive validity of one hypothesis
	 */
	boolean isOnlyOneHypothesisValid();

	/**
	 * Get some pretty printed basic information about this hypotheses set.
	 *
	 * @return some pretty printed information
	 */
	String getShortInfo();

	/**
	 * Identify the word (iff existing) which is related to theses hypotheses. May be {@code null} iff no such word
	 * exists.
	 *
	 * @return the word which belong to the hypotheses
	 * @see    #getHypothesesRange()
	 */
	String getElementOfHypotheses();

	/**
	 * Get the range of these hypotheses.
	 *
	 * @return the range
	 */
	HypothesisRange getHypothesesRange();

	/**
	 * Create a copy of this hypotheses set with a new set of hypotheses.
	 *
	 * @param  hypotheses the new hypotheses
	 * @return            the new hypotheses set
	 */
	IHypothesesSet withHypotheses(List<IHypothesis> hypotheses);

}
