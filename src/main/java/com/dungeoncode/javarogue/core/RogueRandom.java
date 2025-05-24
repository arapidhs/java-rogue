package com.dungeoncode.javarogue.core;

import com.dungeoncode.javarogue.system.death.DeathSource;
import com.dungeoncode.javarogue.template.KillTypeTemplate;
import com.dungeoncode.javarogue.template.MonsterTemplate;
import com.dungeoncode.javarogue.template.Templates;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom random number generator replicating the original Rogue game's RNG behavior.
 * <p>
 * Based on the Rogue C macro definition:
 * <pre>
 * #define RN (((seed = seed*11109+13849) >> 16) & 0xffff)
 * </pre>
 * Every call to {@code rnd(int range)} advances the internal seed deterministically,
 * using 32-bit overflow semantics.
 */
public class RogueRandom {

    /**
     * Internal mutable seed, updated on every random generation.
     */
    private long seed;

    /**
     * Constructs a new {@code RogueRandom} with the specified initial seed.
     *
     * @param seed the initial seed value, typically based on time and process ID
     */
    public RogueRandom(long seed) {
        this.seed = seed;
    }

    /**
     * Resets the internal seed manually.
     * <p>
     * Used in special situations such as enabling wizard mode, where a specific seed
     * is needed for controlled dungeon generation (refer to Rogue's main.c wizard path).
     *
     * @param seed the new seed value to set
     */
    public void reseed(long seed) {
        this.seed = seed;
    }

    /**
     * Randomly selects a death cause from available monster templates, kill type templates,
     * or a provided default kill name.
     *
     * <p>
     * This replicates the original Rogue behavior where death could be attributed
     * to monsters, environmental factors, or a special case (e.g., {@code "Wally the Wonder Badger"}).
     * </p>
     *
     * <p>
     * Selection logic:
     * </p>
     *
     * <ul>
     *     <li>Sources monster names from {@link MonsterTemplate} templates</li>
     *     <li>Sources environmental kill names from {@link KillTypeTemplate} templates</li>
     *     <li>Optionally includes a {@code defaultKillName} if provided</li>
     * </ul>
     *
     * @param defaultKillName the optional fallback name to include in selection; if {@code null}, it is ignored
     * @return a {@link DeathSource} representing the randomly selected death cause
     * @throws IllegalStateException if no death causes are available to select
     * @see Config#getDefaultKillName()
     */
    public DeathSource selectRandomDeathSource(@Nullable final String defaultKillName) throws IllegalStateException {
        final List<DeathSource> sources = new ArrayList<>();

        for (final MonsterTemplate monster : Templates.getTemplates(MonsterTemplate.class)) {
            sources.add(new DeathSource(DeathSource.Type.MONSTER, monster.getId(), monster.getName()));
        }

        for (final KillTypeTemplate killType : Templates.getTemplates(KillTypeTemplate.class)) {
            sources.add(new DeathSource(DeathSource.Type.KILL_TYPE, killType.getId(), killType.getName()));
        }

        if (defaultKillName != null) {
            sources.add(new DeathSource(DeathSource.Type.DEFAULT, 0, defaultKillName));
        }

        if (sources.isEmpty()) {
            throw new IllegalStateException(Messages.ERROR_NO_DEATH_CAUSES);
        }

        final int index = rnd(sources.size());
        return sources.get(index);
    }

    /**
     * Generates a random integer in the range [0, range), replicating Rogue's {@code rnd(int range)} function.
     * If the specified range is zero, returns zero.
     *
     * @param range the upper bound (exclusive) for the random number
     * @return a random integer between 0 (inclusive) and {@code range} (exclusive)
     */
    public int rnd(int range) {
        if (range == 0) {
            return 0;
        }
        return Math.abs(nextRaw()) % range;
    }

    /**
     * Advances the internal seed according to Rogue's original RNG formula,
     * applying 32-bit overflow simulation after each operation.
     *
     * @return a pseudo-random 16-bit integer between 0 and 65535
     */
    private int nextRaw() {
        seed = (seed * 11109 + 13849) & 0xFFFFFFFFL; // Simulate 32-bit signed integer overflow
        return (int) ((seed >> 16) & 0xFFFF);
    }

    /**
     * Generates a random number within a spread around the given value, with a range of +/- 20%.
     * Replicates the <code>spread</code> function from the Rogue C source, producing a value
     * between <code>nm - nm/20</code> and <code>nm - nm/20 + nm/10</code>.
     *
     * @param nm The base number to spread around.
     * @return A random integer within the calculated spread.
     */
    public int spread(int nm) {
        return nm - nm / 20 + rnd(nm / 10);
    }

    /**
     * Rolls a specified number of dice, each with a given number of sides, and returns the sum.
     * Each die roll generates a random value from 1 to sides (inclusive).
     * <p>
     * Equivalent to the <code>roll</code> function in the C Rogue source.
     *
     * @param times The number of dice to roll.
     * @param sides The number of sides per die.
     * @return The total sum of the dice rolls.
     */
    public int roll(int times, final int sides) {
        int total = 0;
        while (times-- > 0) {
            total += rnd(sides) + 1;
        }
        return total;
    }

    public long getSeed() {
        return seed;
    }
}
