package com.dungeoncode.javarogue.main.base;

import com.dungeoncode.javarogue.main.Config;
import com.dungeoncode.javarogue.main.RogueScreen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        final Logger logger = LoggerFactory.getLogger(this.getClass());
        config = new Config(tempDir.toString());
        lenient().when(screen.getConfig()).thenReturn(config);
        lenient().when(screen.getColumns()).thenReturn(config.getTerminalCols());

        // Mock putString to print to System.out
        lenient().doAnswer(invocation -> {
            String string = invocation.getArgument(2);
            logger.info(string);
            return null;
        }).when(screen).putString(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString());
    }

}