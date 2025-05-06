package com.dungeoncode.javarogue.main;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Default game initializer. Responsible for setting up the initial game state.
 */
public class DefaultInitializer implements Initializer {

    @Override
    public void initialize(@Nonnull final GameState gameState) {
        Objects.requireNonNull(gameState);
        initializePlayer(gameState);
        initializeItemData(gameState);

        final boolean isWizard = gameState.getConfig().isWizard();
        gameState.getConfig().setScoring(!isWizard);

        final int levelNum = 1;
        gameState.newLevel(levelNum);
    }

    private void initializePlayer(@Nonnull final GameState gameState) {
        final boolean silent = true;
        final Player player = new Player(gameState.getConfig());
        gameState.setPlayer(player);

        final Food food = new Food();
        gameState.addToPack(food, silent);

        final Armor ringMail = new Armor(ArmorType.RING_MAIL);
        ringMail.setArmorClass(ringMail.getArmorClass() - 1);
        ringMail.addFlag(ItemFlag.ISKNOW);
        player.setCurrentArmor(ringMail);
        gameState.addToPack(ringMail, silent);

        final Weapon mace = gameState.getWeaponsFactory().initializeWeapon(
                WeaponType.MACE);
        mace.setHitPlus(1);
        mace.setDamagePlus(1);
        mace.addFlag(ItemFlag.ISKNOW);
        gameState.addToPack(mace, silent);
        player.setCurrentWeapon(mace);

        final Weapon bow = gameState.getWeaponsFactory().initializeWeapon(
                WeaponType.SHORT_BOW);
        bow.setHitPlus(1);
        bow.addFlag(ItemFlag.ISKNOW);
        gameState.addToPack(bow, silent);

        final Weapon arrow = gameState.getWeaponsFactory().initializeWeapon(
                WeaponType.ARROW);
        arrow.setCount(gameState.getRogueRandom().rnd(15) + 25);
        arrow.addFlag(ItemFlag.ISKNOW);
        gameState.addToPack(arrow, silent);
    }

    private void initializeItemData(@Nonnull final GameState gameState) {
        gameState.getItemData().init();
    }

}
