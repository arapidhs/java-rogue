package com.dungeoncode.javarogue.core;

import org.apache.commons.codec.digest.DigestUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class RogueUtils {

    /**
     * Number-to-word mapping for use in UI (e.g. {@code "Top Ten Scores"}).
     * <p>
     * This replaces the use of the C variable {@code Numname} in {@code rip.c}, which held the word version of
     * the number of top scores to display. Used in title strings like:
     * <pre>
     *     Top Ten Scores:
     * </pre>
     * Only values 0–20 are supported.
     */

    private static final String[] NUM_WORDS = {
            "Zero", "One", "Two", "Three", "Four", "Five",
            "Six", "Seven", "Eight", "Nine", "Ten",
            "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen",
            "Sixteen", "Seventeen", "Eighteen", "Nineteen", "Twenty"
    };


    /**
     * Converts a small number (0–20) to its English word equivalent.
     * <p>
     * Used for rendering phrases like {@code "Top Ten Scores"} on the score screen.
     * This is the Java equivalent of how the original Rogue C code used the {@code Numname} variable,
     * which was set based on the number of scores displayed.
     * </p>
     *
     * @param n the number to convert
     * @return the word version of the number, or the number itself as a string if out of range
     */
    public static String numberToWord(int n) {
        if (n >= 0 && n < NUM_WORDS.length) {
            return NUM_WORDS[n];
        }
        // fallback: just return the number
        return String.valueOf(n);
    }

    public static boolean isEmpty(final String str) {
        return str == null || str.isEmpty();
    }

    public static String limitLength(String input, int maxLength) {
        if (input == null) {
            return null;
        }
        return input.length() <= maxLength
                ? input
                : input.substring(0, maxLength);
    }

    /**
     * Returns the correct indefinite article ("a" or "an") for the given word,
     * based on whether it starts with a vowel sound.
     *
     * @param word The word to check
     * @return "an" if the word starts with a vowel, otherwise "a"
     */
    public static String getIndefiniteArticleFor(@Nullable final String word) {
        if (word == null || word.isEmpty()) {
            return "a";
        }

        final char first = Character.toLowerCase(word.charAt(0));
        return switch (first) {
            case 'a', 'e', 'i', 'o', 'u' -> "an";
            default -> "a";
        };
    }

    public static String crypt(@Nonnull final String input, @Nonnull final String salt) {
        Objects.requireNonNull(input);
        Objects.requireNonNull(salt);
        return DigestUtils.sha256Hex(salt + input);
    }

    /**
     * Applies XOR-based symmetric encryption/decryption on the given byte array.
     * <p>
     * This method mimics the behavior of the original Rogue's `encread`/`encwrite`
     * routine. It uses two key arrays (primary and secondary) and a feedback byte
     * to produce a simple stream cipher. The same method is used for both encryption
     * and decryption because XOR operations are symmetric.
     *
     * @param data The byte array to encrypt or decrypt, modified in place.
     * @param key1 The primary encryption key (equivalent to `encstr` in C).
     * @param key2 The secondary encryption key (equivalent to `statlist` in C).
     */
    public static void xorCrypt(byte[] data, byte[] key1, byte[] key2) {
        int fb = 0;            // Feedback byte for chaining encryption
        int e1Index = 0;       // Index into key1
        int e2Index = 0;       // Index into key2

        for (int i = 0; i < data.length; i++) {
            // XOR the byte with key1, key2, and feedback
            data[i] ^= (byte) (key1[e1Index] ^ key2[e2Index] ^ fb);

            // Calculate new feedback based on current key bytes
            int temp = Byte.toUnsignedInt(key1[e1Index]);
            fb += temp * Byte.toUnsignedInt(key2[e2Index]);

            // Cycle key indices (wrap around if necessary)
            e1Index = (e1Index + 1) % key1.length;
            e2Index = (e2Index + 1) % key2.length;
        }
    }

}
