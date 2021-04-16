package edu.kit.ipd.eagle.impl.xplore.layer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.kit.ipd.eagle.port.IAgent;
import edu.kit.ipd.eagle.port.IDataStructure;
import edu.kit.ipd.eagle.port.hypothesis.IHypothesesSelection;
import edu.kit.ipd.eagle.port.hypothesis.IHypothesesSet;
import edu.kit.ipd.eagle.port.xplore.layer.ILayerEntry;
import edu.kit.ipd.eagle.port.xplore.selection.ISelectionProvider;

/**
 * Defines the realization of an {@link ILayerEntry}.
 *
 * @author Dominik Fuchss
 * @param <A>  the type of agent to use
 * @param <DS> the type of data structure to use
 */
public final class LayerEntry<A extends IAgent<DS>, DS extends IDataStructure<DS>> implements ILayerEntry {
    private static final long serialVersionUID = -349086188573289109L;

    private transient Layer<A, DS> layer;
    private transient int entryNo;
    private transient DS inputData;
    private transient DS evaluatedData;

    private transient LayerEntry<A, DS> parent;

    private List<IHypothesesSet> hypotheses;

    private transient Map<List<IHypothesesSelection>, LayerEntry<A, DS>> nextLayerEntries = new HashMap<>();

    LayerEntry(Layer<A, DS> layer, int number, LayerEntry<A, DS> parent, DS input) {
        this.layer = layer;
        this.entryNo = number;
        this.inputData = input;
        this.parent = parent;
    }

    /**
     * Create the internal data of the layer entry by invoke of agent.
     */
    public void create() {
        var instance = this.layer.getAgent().createAgentInstance();

        if (ILayerEntry.logger.isDebugEnabled()) {
            ILayerEntry.logger.debug("Run analysis for " + this.layer + ", " + this.layer.getAgent());
        }

        this.evaluatedData = instance.execute(this.inputData);

        if (ILayerEntry.logger.isDebugEnabled()) {
            ILayerEntry.logger.debug("Finished analysis for " + this.layer + ", " + this.layer.getAgent());
        }

        if (this.layer.getAgentWithHypotheses() != null) {
            this.hypotheses = this.layer.getAgentWithHypotheses().getHypothesesFromDataStructure(this.evaluatedData);
        }
    }

    void explore(ISelectionProvider selectionProvider) {
        if (this.hypotheses == null && selectionProvider != null) {
            throw new UnsupportedOperationException("No hypotheses for " + this);
        }
        if (this.hypotheses == null) {
            // No hypotheses ..
            this.createNextBySelection(null);
            return;
        }
        if (this.hypotheses.isEmpty()) {
            ILayerEntry.logger.warn("No hypotheses were generated by " + this);
        }
        var selections = selectionProvider.findSelection(this.hypotheses);
        for (var selection : selections) {
            this.createNextBySelection(selection);
        }
    }

    @Override
    public List<IHypothesesSet> getHypotheses() {
        return this.hypotheses == null ? List.of() : new ArrayList<>(this.hypotheses);
    }

    void createNextBySelection(List<IHypothesesSelection> selection) {
        if (this.layer.getNext() == null) {
            throw new UnsupportedOperationException("This is a leaf ..");
        }

        if (selection == null && this.layer.getAgentWithHypotheses() == null) {
            this.createNoHypothesesNext();
            return;
        }

        if (selection == null) {
            throw new UnsupportedOperationException("Selection mustn't be null");
        }

        this.createHypothesesNext(selection);

    }

    private LayerEntry<A, DS> createNoHypothesesNext() {
        if (!this.nextLayerEntries.isEmpty()) {
            throw new UnsupportedOperationException("NoHypotheses LayerEntry already explored.");
        }
        var newEntry = this.layer.getNext().addEmptyEntry(this, this.evaluatedData.createCopy());
        this.nextLayerEntries.put(null, newEntry);
        newEntry.create();
        return newEntry;
    }

    private void createHypothesesNext(List<IHypothesesSelection> selection) {
        if (this.nextLayerEntries.containsKey(selection)) {
            return;
        }

        ILayerEntry.logger.debug("Appling selection " + selection + " @ " + this.layer);
        var resultData = this.inputData.createCopy();
        this.layer.getAgentWithHypotheses().applyHypothesesToDataStructure(resultData, selection);

        var newEntry = this.layer.getNext().addEmptyEntry(this, resultData);
        this.nextLayerEntries.put(selection, newEntry);
        if (ILayerEntry.logger.isDebugEnabled()) {
            ILayerEntry.logger.debug("Created empty layer entry " + newEntry);
        }
        newEntry.create();
        if (ILayerEntry.logger.isInfoEnabled()) {
            ILayerEntry.logger.info("Created layer entry " + newEntry);
        }
    }

    private List<IHypothesesSelection> findSelection(LayerEntry<A, DS> layerEntry) {
        for (var entry : this.nextLayerEntries.entrySet()) {
            if (entry.getValue() == layerEntry) {
                return entry.getKey() == null ? null : new ArrayList<>(entry.getKey());
            }
        }
        return null;
    }

    @Override
    public boolean isLeaf() {
        return this.layer.getNext() == null;
    }

    @Override
    public List<ILayerEntry> getChildren() {
        return new ArrayList<>(this.nextLayerEntries.values());
    }

    @Override
    public List<IHypothesesSelection> getSelectionsFromBefore() {
        return this.parent == null ? null : this.parent.findSelection(this);
    }

    @Override
    public ILayerEntry[] getPathToRoot() {
        List<ILayerEntry> pathToRoot = new ArrayList<>();
        pathToRoot.add(this);

        LayerEntry<A, DS> before = this.parent;
        while (before != null) {
            pathToRoot.add(before);
            before = before.parent;
        }
        return pathToRoot.toArray(ILayerEntry[]::new);
    }

    @Override
    public String toString() {
        return "LayerEntry [id=" + this.getId() + ", " //
                + "agent=" + (this.layer.getAgent() == null ? null : this.layer.getAgent().getClass().getSimpleName()) + ", " //
                + "selectionsFromBefore=" + this.getSelectionsFromBefore() + ", " //
                + "hypotheses=" + this.getHypotheses() + ", " //
                + "]";
    }

    @Override
    public String getId() {
        return this.layer.getAgent().getClass().getSimpleName() + "-" + this.entryNo;
    }

    @Override
    public String getAgent() {
        return this.layer.getAgent().getClass().getSimpleName();
    }

    @Override
    public String getStateRepresentationBeforeExecution() {
        return this.inputData.getStateRepresentation();
    }

}
