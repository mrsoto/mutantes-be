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
     *
     * @param dna DNA to be tested
     * @return {@code true} when no mutation exists, {@code false} when a mutation is identified
     */
    @Override
    public boolean isHuman(@NonNull Collection<String> dna) {
        return !isMutant(dna);
    }

    private boolean isMutant(Collection<String> dna) {
        final int colSize = dna.stream().findFirst().map(String::length).orElseThrow();
        int dnaSize = dna.size();
        int[] horizontalCount = new int[dnaSize];
        int[] verticalCount = new int[colSize];
        int[] norWestCount = new int[colSize * dnaSize];
        int[] norEstCount = new int[colSize * dnaSize];
        String[] sequences = new String[dnaSize];
        sequences = dna.toArray(sequences);

        boolean notFirstRow = false;
        for (int row = 0; row < dnaSize; row++) {
            boolean notFirstCol = false;
            for (int col = 0; col < colSize; col++) {
                int current = sequences[row].codePointAt(col);
                if (notFirstCol && sequences[row].codePointAt(col - 1) == current) {
                    horizontalCount[row]++;
                    if (horizontalCount[row] == MUTATION_REPETITION_COUNT) return true;
                } else {
                    horizontalCount[row] = 1;
                }
                if (notFirstRow && sequences[row - 1].codePointAt(col) == current) {
                    verticalCount[col]++;
                    if (verticalCount[col] == MUTATION_REPETITION_COUNT) return true;
                } else {
                    verticalCount[col] = 1;
                }
                if (notFirstCol && notFirstRow && sequences[row - 1].codePointAt(col - 1) == current) {
                    norWestCount[row * colSize + col] =
                            norWestCount[(row - 1) * colSize + (col - 1)] + 1;
                    if (norWestCount[row * colSize + col] == MUTATION_REPETITION_COUNT - 1)
                        return true;
                }
                if (col < colSize - 1 && notFirstRow && sequences[row - 1].codePointAt(col + 1) == current) {
                    norEstCount[row * colSize + col] =
                            norEstCount[(row - 1) * colSize + (col + 1)] + 1;
                    if (norEstCount[row * colSize + col] == MUTATION_REPETITION_COUNT - 1)
                        return true;
                }
                notFirstCol = true;
            }
            notFirstRow = true;
        }

        return false;
    }

}
