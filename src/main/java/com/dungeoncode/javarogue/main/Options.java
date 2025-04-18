package com.dungeoncode.javarogue.main;

import picocli.CommandLine;

public class Options {

    @CommandLine.Option(names = "-master", description = "Enable master switch (true/false)")
    Boolean master = false;

    @CommandLine.Option(names = "-terse", description = "Terse output (true/false)")
    Boolean terse = false;

    @CommandLine.Option(names = "-flush", description = "Flush type ahead during battle (true/false)")
    Boolean flush = false;

    @CommandLine.Option(names = "-seefloor", description = "Show the lamp-illuminated floor (true/false)")
    Boolean seeFloor = true;

    @CommandLine.Option(names = "-passgo", description = "Follow turnings in passageways (true/false)")
    Boolean passGo = false;

    @CommandLine.Option(names = "-tombstone", description = "Print tombstone when killed (true/false)")
    Boolean tombstone = true;

    @CommandLine.Option(names = "-showScores", description = "Print high scores and exit (true/false)")
    Boolean showScores = false;

    @CommandLine.Option(
            names = "-allowMultipleScores",
            description = "Allow multiple entries from the same player in the high score list (true/false)"
    )
    Boolean allowMultipleScores = true;

    @CommandLine.Option(names = "-name", description = "Player name")
    String name;

    @CommandLine.Option(names = "-fruit", description = "Favorite fruit")
    String fruit;

    @CommandLine.Option(names = "-file", description = "Save file path")
    String file;

    @CommandLine.Option(names = "-seed", description = "Dungeon seed for RNG")
    Integer seed;

}
