package com.dungeoncode.javarogue.main;

import com.dungeoncode.javarogue.main.base.RogueBaseTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScoreManagerTest extends RogueBaseTest {

    @TempDir
    Path tempDir;

    @Test
    void testReadWrite() throws IOException {

        Mockito.when(screen.getConfig()).thenReturn(config);
        final ScoreManager scoreManager = new ScoreManager(screen);

        final List<ScoreManager.ScoreEntry> testEntries = List.of(
                new ScoreManager.ScoreEntry(1001, 5000, GameEndReason.fromId(0), 3, 0, 10, 168000001, "hero1"),
                new ScoreManager.ScoreEntry(1002, 4200, GameEndReason.fromId(2), 5, 0, 8, 168000002, "hero2"),
                new ScoreManager.ScoreEntry(1003, 2500, GameEndReason.fromId(2), 0, 2, 5, 168000003, "hero3"));
        scoreManager.writeScoreFile(testEntries);

        final List<ScoreManager.ScoreEntry> scoreEntries = scoreManager.readScoreFile();
        assertEquals(testEntries.size(), scoreEntries.size());

        // Ensure all written entries are found in the read entries
        testEntries.forEach(expected ->
                assertTrue(scoreEntries.contains(expected), "Missing entry: " + expected));
    }

}
