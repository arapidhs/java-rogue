package com.dungeoncode.javarogue.main;

import com.dungeoncode.javarogue.system.entity.item.Armor;
import com.dungeoncode.javarogue.system.entity.item.ArmorType;
import com.dungeoncode.javarogue.system.SymbolType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArmorTest {

    @Test
    void testNum() {
        final Armor armor = new Armor(ArmorType.SPLINT_MAIL);
        final int baseArmorClass = armor.getArmorClass();

        String numArmorClass = "+0";
        assertEquals(numArmorClass, armor.num());

        // Assign negative bonus ( higher armor class is worse)
        armor.setArmorClass(baseArmorClass + 2);
        numArmorClass = "-2";
        assertEquals(numArmorClass, armor.num());

        // Assign positive bonus
        armor.setArmorClass(baseArmorClass - 1);
        numArmorClass = "+1";
        assertEquals(numArmorClass, armor.num());

        assertEquals(SymbolType.ARMOR,armor.getSymbolType());
    }

}
