package com.dungeoncode.javarogue.main;

import com.dungeoncode.javarogue.core.Config;
import com.dungeoncode.javarogue.core.RogueFactory;
import com.dungeoncode.javarogue.core.RogueRandom;
import com.dungeoncode.javarogue.system.SymbolType;
import com.dungeoncode.javarogue.system.entity.item.Weapon;
import com.dungeoncode.javarogue.system.entity.item.WeaponType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WeaponTest {

    @Test
    void numTest() {
        final Config config = new Config();
        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
        final RogueFactory rogueFactory = new RogueFactory(config, rogueRandom);

        final Weapon mace = rogueFactory.initWeapon(WeaponType.MACE);
        final String zeroBonus = "+0,+0";
        assertEquals(zeroBonus, mace.num());

        mace.setHitPlus(-1);
        mace.setDamagePlus(+2);
        final String negativePositiveBonus = "-1,+2";
        assertEquals(negativePositiveBonus, mace.num());

        assertEquals(SymbolType.WEAPON, mace.getSymbolType());
    }

}
