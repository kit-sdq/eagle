package edu.kit.ipd.eagle.port.xplore.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import edu.kit.ipd.eagle.port.hypothesis.IHypothesesSelection;
import edu.kit.ipd.eagle.port.hypothesis.IHypothesesSet;
import edu.kit.ipd.eagle.port.xplore.layer.ILayerEntry;

/**
 * Defines the data transfer object for {@link ILayerEntry}.
 *
 * @author Dominik Fuchss
 *
 */
public final class LayerEntryDTO implements ILayerEntry {

	private static final long serialVersionUID = -619310410950224490L;

	private List<IHypothesesSelection> selectionsFromBefore;
	private List<ILayerEntry> children;
	private List<IHypothesesSet> hypotheses;
	private String agent;
	private String id;
	private String stateRepresentationBeforeExecution;

	@Override
	public List<IHypothesesSelection> getSelectionsFromBefore() {
		return this.selectionsFromBefore;
	}

	/**
	 * Setter for {@link #getSelectionsFromBefore()}.
	 *
	 * @param selectionsFromBefore the selections from the step before
	 */
	@JsonDeserialize(contentAs = HypothesesSelectionDTO.class)
	public void setSelectionsFromBefore(List<IHypothesesSelection> selectionsFromBefore) {
		this.selectionsFromBefore = selectionsFromBefore;
	}

	@Override
	public List<ILayerEntry> getChildren() {
		if (this.children == null) {
			return null;
		}

		List<ILayerEntry> steps = new ArrayList<>(this.children);
		Collections.sort(steps, (a, b) -> a.getId().compareTo(b.getId()));
		return steps;
	}

	/**
	 * Setter for {@link #getChildren()}.
	 *
	 * @param children the children of this step
	 */
	@JsonDeserialize(contentAs = LayerEntryDTO.class)
	public void setChildren(List<ILayerEntry> children) {
		this.children = children;
	}

	@Override
	public List<IHypothesesSet> getHypotheses() {
		return this.hypotheses;
	}

	/**
	 * Setter for {@link #getHypotheses()}.
	 *
	 * @param hypotheses the hypotheses of this step
	 */
	@JsonDeserialize(contentAs = HypothesesSetDTO.class)
	public void setHypotheses(List<IHypothesesSet> hypotheses) {
		this.hypotheses = hypotheses;
	}

	@Override
	public String getAgent() {
		return this.agent;
	}

	/**
	 * Setter for {@link #getAgent()}.
	 *
	 * @param agent the name of the agent of this step
	 */
	public void setAgent(String agent) {
		this.agent = agent;
	}

	@Override
	public String getId() {
		return this.id;
	}

	/**
	 * Setter for {@link #getId()}.
	 *
	 * @param id the id of this step
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Setter for {@link #getStateRepresentationBeforeExecution()}.
	 *
	 * @param stateRepresentationBeforeExecution the state representation before execution
	 */
	public void setStateRepresentationBeforeExecution(String stateRepresentationBeforeExecution) {
		this.stateRepresentationBeforeExecution = stateRepresentationBeforeExecution;
	}

	@Override
	public String getStateRepresentationBeforeExecution() {
		return this.stateRepresentationBeforeExecution;
	}

	@Override
	public boolean isLeaf() {
		throw new UnsupportedOperationException("Not supported by DTO");
	}

	@Override
	public ILayerEntry[] getPathToRoot() {
		throw new UnsupportedOperationException("Not supported by DTO");
	}

}
