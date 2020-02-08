package me.mrs.mutantes.servicios;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class DnaEvaluatorImpl implements DnaEvaluator {

    public static final int MUTATION_REPETITION_COUNT = 4;

    /**
     * Evaluate a DNA looking for mutations. A mutation is identified by for linearly continuous
     * symbols in any direction.
     * <p>
     * Preconditions: DNA should be valid, See {@link DnaValidator}
     * <p>Algorithm <b>O(S x B)</b> or <b>O(N<sup>2</sup>)</b> or less for an ADN of
     * {@code S} sequences and  {@code B} bases per sequence.
     * <p>
     * Every single base of a given ADN is compared at most with 4 adjacent bases (NE, N, NW,
     * E).</p>
     * <p>
     *
     * <p><em>Notas:</em>
     * <ul><li>
     *     Consumo de memoria <b>2 * O(NxM)</b>
     * </li>
     * <li>Priorizado el performance por encima de la complejidad</li>
     * <li>Asume que el adn está correctamente definido</li>
     * </ul>
     * </p>
     *
     * @param dna DNA to be tested
     * @return {@code true} when no mutation exists, {@code false} when a mutation is identified
     */
    @Override
    public boolean isHuman(@NonNull Collection<String> dna) {
        return !isMutant(dna);
    }

    /**
     * Look for a mutation. Prioritized performance over simplicity as required.
     *
     * @param dna DNA Sequence
     * @return {@code true} when a mutation is found
     */
    private boolean isMutant(Collection<String> dna) {
        final int colSize = dna.stream().findFirst().map(String::length).orElseThrow();
        final int dnaSize = dna.size();
        final int[] horizontalCount = new int[dnaSize];
        final int[] verticalCount = new int[colSize];
        final int[] norWestCount = new int[colSize * dnaSize];
        final int[] norEstCount = new int[colSize * dnaSize];
        final String[] sequences = dna.toArray(new String[dnaSize]);

        boolean notFirstRow = false;
        for (int row = 0; row < dnaSize; row++) { // O(N)
            boolean notFirstCol = false;
            for (int col = 0; col < colSize; col++) { // O(N)
                int currentBase = baseAt(sequences, row, col); // O(1)
                if (notFirstCol && baseAt(sequences, row, col - 1) == currentBase) {
                    horizontalCount[row]++;
                    if (horizontalCount[row] == MUTATION_REPETITION_COUNT) return true;
                } else {
                    horizontalCount[row] = 1;
                }
                if (notFirstRow && baseAt(sequences, row - 1, col) == currentBase) {
                    verticalCount[col]++;
                    if (verticalCount[col] == MUTATION_REPETITION_COUNT) return true;
                } else {
                    verticalCount[col] = 1;
                }
                if (notFirstCol && notFirstRow && baseAt(sequences, row - 1, col - 1) == currentBase) {
                    norWestCount[row * colSize + col] = norWestCount[(row - 1) * colSize + (col - 1)] + 1;
                    if (norWestCount[row * colSize + col] == MUTATION_REPETITION_COUNT - 1) return true;
                }
                if (col < colSize - 1 && notFirstRow && baseAt(sequences, row - 1, col + 1) == currentBase) {
                    norEstCount[row * colSize + col] = norEstCount[(row - 1) * colSize + (col + 1)] + 1;
                    if (norEstCount[row * colSize + col] == MUTATION_REPETITION_COUNT - 1) return true;
                }
                notFirstCol = true;
            }
            notFirstRow = true;
        }

        return false;
    }

    private int baseAt(String[] sequences, int row, int col) {
        return sequences[row].codePointAt(col);
    }

}
