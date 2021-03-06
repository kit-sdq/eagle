package edu.kit.ipd.eagle.port;

/**
 * Defines a data structure for agents.
 *
 * @author Dominik Fuchss
 *
 * @param <DS> just use {@code class Data implements IDataStructure<Data>}
 */
public interface IDataStructure<DS extends IDataStructure<DS>> {
    /**
     * Create a clone of the data structure.
     *
     * @return a clone of the data structure
     */
    DS createCopy();

    /**
     * Get a readable state representation of the current data structure.<br>
     * Iff {@code null} the result will be ignored.
     *
     * @return a readable state representation
     */
    String getStateRepresentation();

}
