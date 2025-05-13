package com.dungeoncode.javarogue.system.initializer;

import com.dungeoncode.javarogue.core.Config;
import com.dungeoncode.javarogue.core.GameState;
import com.dungeoncode.javarogue.core.Phase;
import com.dungeoncode.javarogue.system.entity.creature.Player;
import com.dungeoncode.javarogue.system.entity.item.ItemFlag;
import com.dungeoncode.javarogue.system.entity.item.Armor;
import com.dungeoncode.javarogue.system.entity.item.ArmorType;
import com.dungeoncode.javarogue.system.entity.item.Food;
import com.dungeoncode.javarogue.system.entity.item.Weapon;
import com.dungeoncode.javarogue.system.entity.item.WeaponType;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Objects;

/**
 * Default implementation of the {@link Initializer} interface, responsible for setting up
 * the initial game state. Configures the player with starting items, initializes item data,
 * sets up the game sub window, and prepares the game for level 1 with all phases enabled.
 * <p>
 * Mirrors the initial setup logic in the C Rogue source, such as player and item initialization
 * in <code>init.c</code> and level setup in <code>main.c</code>.
 * </p>
 */
public class DefaultInitializer implements Initializer {

    /**
     * Initializes the game state by setting up the player, item data, game window, scoring,
     * first level, and enabling all phases.
     *
     * @param gameState The game state to initialize.
     * @throws NullPointerException if gameState is null.
     */
    @Override
    public void initialize(@Nonnull final GameState gameState) {
        Objects.requireNonNull(gameState);
        final Config config = gameState.getConfig();

        initializePlayer(gameState);
        initializeItemData(gameState);

        // Set up the game sub window with full terminal dimensions
        gameState.getScreen().addWindow("hw", 0, 0, config.getTerminalCols(), config.getTerminalRows());

        // Disable scoring in wizard mode for master configuration
        if (config.isMaster()) {
            final boolean isWizard = config.isWizard();
            config.setScoring(!isWizard);
        }

        // Start at level 1
        gameState.setNoFood(0);
        final int levelNum = 1;
        gameState.newLevel(levelNum);

        // Enable all game phases
        Arrays.stream(Phase.values()).forEach(gameState::enablePhase);
    }

    /**
     * Initializes the player with starting items: food, ring mail armor, mace, short bow,
     * and arrows. Configures item properties (e.g., bonuses, flags) and adds them to the
     * player's inventory silently. Sets initial game end reason, death source, and gold to
     * zero.
     *
     * @param gameState The game state containing the player and inventory.
     * @throws NullPointerException if gameState is null.
     */
    private void initializePlayer(@Nonnull final GameState gameState) {
        Objects.requireNonNull(gameState);
        final boolean silent = true;
        final Player player = new Player(gameState.getConfig());
        gameState.setPlayer(player);

        // Add food to inventory
        final Food food = new Food();
        gameState.addToPack(food, silent);

        // Equip and add ring mail armor with adjusted armor class
        final Armor ringMail = new Armor(ArmorType.RING_MAIL);
        ringMail.setArmorClass(ringMail.getArmorClass() - 1);
        ringMail.addFlag(ItemFlag.ISKNOW);
        player.setCurrentArmor(ringMail);
        gameState.addToPack(ringMail, silent);

        // Equip and add mace with hit and damage bonuses
        final Weapon mace = gameState.getRogueFactory().initWeapon(WeaponType.MACE);
        mace.setHitPlus(1);
        mace.setDamagePlus(1);
        mace.addFlag(ItemFlag.ISKNOW);
        gameState.addToPack(mace, silent);
        player.setCurrentWeapon(mace);

        // Add short bow with hit bonus
        final Weapon bow = gameState.getRogueFactory().initWeapon(WeaponType.SHORT_BOW);
        bow.setHitPlus(1);
        bow.addFlag(ItemFlag.ISKNOW);
        gameState.addToPack(bow, silent);

        // Add arrows with random quantity (25-39)
        final Weapon arrow = gameState.getRogueFactory().initWeapon(WeaponType.ARROW);
        arrow.setCount(gameState.getRogueRandom().rnd(15) + 25);
        arrow.addFlag(ItemFlag.ISKNOW);
        gameState.addToPack(arrow, silent);

        // Initialize game end and gold
        gameState.setGameEndReason(null);
        gameState.setDeathSource(null);
        gameState.getPlayer().setGoldAmount(0);
    }

    /**
     * Initializes the item data for the game state by calling the item data’s initialization method.
     *
     * @param gameState The game state containing the item data.
     * @throws NullPointerException if gameState is null.
     */
    private void initializeItemData(@Nonnull final GameState gameState) {
        Objects.requireNonNull(gameState);
        gameState.getItemData().init();
    }
}