package com.dungeoncode.javarogue.main.base;

import com.dungeoncode.javarogue.main.Config;
import com.dungeoncode.javarogue.main.RogueScreen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;

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
        Mockito.when(screen.getConfig()).thenReturn(config);
    }

}