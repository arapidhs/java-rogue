package com.dungeoncode.javarogue.main.base;

import com.dungeoncode.javarogue.core.Config;
import com.dungeoncode.javarogue.ui.RogueScreen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;

import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public abstract class RogueBaseTest {

    @TempDir
    protected Path tempDir;

    @Mock
    protected RogueScreen screen;

    protected Config config;

    @BeforeEach
    void setUp() {
        config = new Config(tempDir.toString());
        lenient().when(screen.getConfig()).thenReturn(config);
    }

}