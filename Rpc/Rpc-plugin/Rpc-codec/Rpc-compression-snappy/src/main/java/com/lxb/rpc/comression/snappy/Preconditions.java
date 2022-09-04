package com.lxb.rpc.comression.snappy;


/**
 * 检查
 */
public class Preconditions {

    protected Preconditions() {
    }

    /**
     * Ensures that {@code start} and {@code end} specify a valid <i>positions</i> in an array, list
     * or string of size {@code size}, and are in order. A position index may range from zero to
     * {@code size}, inclusive.
     *
     * @param start a user-supplied index identifying a starting position in an array, list or string
     * @param end   a user-supplied index identifying a ending position in an array, list or string
     * @param size  the size of that array, list or string
     * @throws IndexOutOfBoundsException if either index is negative or is greater than {@code size},
     *                                   or if {@code end} is less than {@code start}
     * @throws IllegalArgumentException  if {@code size} is negative
     */
    public static void checkPositionIndexes(final int start, final int end, final int size) {
        // Carefully optimized for execution by hotspot (explanatory comment above)
        if (start < 0) {
            throw new IndexOutOfBoundsException(String.format("start index (%d) must not be negative", start));
        } else if (end < start) {
            throw new IndexOutOfBoundsException(String.format("end index (%d) must not be less than start index (%d)", end, start));
        } else if (end > size) {
            throw new IndexOutOfBoundsException(String.format("end index (%d) must not be greater than size (%d)", end, size));
        }
    }

}
