package me.mrs.mutantes.services;

import me.mrs.mutantes.DnaEvaluator;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public class DnaEvaluatorImpl implements DnaEvaluator {

    public static final int MUTATION_REPETITION_COUNT = 4;

    /**
     * Evaluate a DNA looking for mutations. A mutation is identified by for linearly continuous
     * symbols in any direction.
     * <p>
     *
     * <p>Algorithm <b>O(S x B)</b> or <b>O(N<sup>2</sup>)</b> or less for an ADN of
     * {@code S} sequences and  {@code B} bases per sequence.
     * <p>
     * Every single base of a given ADN is compared at most with 4 adjacent bases (NE, N, NW,
     * E).</p>
     * <p>
     *
     * <p><em>Notes:</em>
     * <ul><li>
     *     Memory ussage <b>2 * O(NxM)</b>
     * </li>
     * <li>Performance priorized over complexity</li>
     * <li>Preconditions: DNA should be valid, See {@code DnaValidator}</li>
     * </ul>
     * </p>
     *
     * @param dna DNA to be tested
     * @return {@code true} when no mutation exists, {@code false} when a mutation is identified
     */
    @Override
    public boolean isMutant(@NotNull List<String> dna) {
        return isMutant(dna.toArray(new String[0]));
    }

    private static int baseAt(String[] dna, int row, int col) {
        return dna[row].codePointAt(col);
    }

    /**
     * Look for a mutation. Prioritized performance over simplicity as required.
     *
     * @param dna DNA sequence
     * @return {@code true} when a mutation is found
     */
    @Override
    @SuppressWarnings({"squid:MethodCyclomaticComplexity", "squid:S3776", "squid:S134"})
    public boolean isMutant(String[] dna) {
        final int colSize = dna[0].length();
        final int dnaSize = dna.length;
        final int[] horizontalCount = new int[dnaSize];
        final int[] verticalCount = new int[colSize];
        final int[][] norWestCount = new int[dnaSize][colSize];
        final int[][] norEstCount = new int[dnaSize][colSize];
        int founds = 0;

        for (int row = 0; row < dnaSize && founds < 2; row++) { // O(N)
            boolean notFirstCol = false;
            for (int col = 0; col < colSize && founds < 2; col++, notFirstCol = true) { // O(N)
                int currentBase = baseAt(dna, row, col); // O(1)

                if (notFirstCol && baseAt(dna, row, col - 1) == currentBase) {
                    horizontalCount[row]++;
                    if (horizontalCount[row] == MUTATION_REPETITION_COUNT) {
                        founds++;
                    }
                } else {
                    horizontalCount[row] = 1;
                }

                if (row != 0 && baseAt(dna, row - 1, col) == currentBase) {
                    verticalCount[col]++;
                    if (verticalCount[col] == MUTATION_REPETITION_COUNT) {
                        founds++;
                    }
                } else {
                    verticalCount[col] = 1;
                }
                if (row != 0) {
                    if (notFirstCol && baseAt(dna, row - 1, col - 1) == currentBase) {
                        norWestCount[row][col] = norWestCount[row - 1][col - 1] + 1;
                        if (norWestCount[row][col] == MUTATION_REPETITION_COUNT - 1) {
                            founds++;
                        }
                    }

                    boolean notLastCol = col < colSize - 1;
                    if (notLastCol && baseAt(dna, row - 1, col + 1) == currentBase) {
                        norEstCount[row][col] = norEstCount[row - 1][col + 1] + 1;
                        if (norEstCount[row][col] == MUTATION_REPETITION_COUNT - 1) {
                            founds++;
                        }
                    }
                }
            }
        }

        return founds == 2;
    }

}
