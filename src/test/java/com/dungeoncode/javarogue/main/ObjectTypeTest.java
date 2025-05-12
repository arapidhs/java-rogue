package com.dungeoncode.javarogue.main;

import com.dungeoncode.javarogue.system.entity.item.ObjectType;
import com.dungeoncode.javarogue.system.entity.item.Amulet;
import com.dungeoncode.javarogue.system.entity.item.Armor;
import com.dungeoncode.javarogue.system.entity.item.ArmorType;
import com.dungeoncode.javarogue.system.entity.item.Food;
import com.dungeoncode.javarogue.system.entity.item.Gold;
import com.dungeoncode.javarogue.system.entity.item.Potion;
import com.dungeoncode.javarogue.system.entity.item.PotionType;
import com.dungeoncode.javarogue.system.entity.item.Ring;
import com.dungeoncode.javarogue.system.entity.item.RingType;
import com.dungeoncode.javarogue.system.entity.item.Rod;
import com.dungeoncode.javarogue.system.entity.item.RodType;
import com.dungeoncode.javarogue.system.entity.item.Scroll;
import com.dungeoncode.javarogue.system.entity.item.ScrollType;
import com.dungeoncode.javarogue.system.entity.item.Weapon;
import com.dungeoncode.javarogue.system.entity.item.WeaponType;
import com.dungeoncode.javarogue.system.SymbolType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObjectTypeTest {

    @Test
    void testObjectTypes(){
        final Armor armor = new Armor(ArmorType.SPLINT_MAIL);
        assertEquals(ObjectType.ARMOR,armor.getObjectType());
        assertEquals(SymbolType.ARMOR,armor.getSymbolType());

        final Potion potion = new Potion(PotionType.HASTE_SELF);
        assertEquals(ObjectType.POTION,potion.getObjectType());
        assertEquals(SymbolType.POTION,potion.getSymbolType());

        final Scroll scroll = new Scroll(ScrollType.SCARE_MONSTER);
        assertEquals(ObjectType.SCROLL,scroll.getObjectType());
        assertEquals(SymbolType.SCROLL,scroll.getSymbolType());

        final Food food = new Food();
        assertEquals(ObjectType.FOOD,food.getObjectType());
        assertEquals(SymbolType.FOOD,food.getSymbolType());

        final Ring ring = new Ring(RingType.R_NOP);
        assertEquals(ObjectType.RING,ring.getObjectType());
        assertEquals(SymbolType.RING,ring.getSymbolType());

        final Weapon weapon = new Weapon(WeaponType.TWO_HANDED_SWORD);
        assertEquals(ObjectType.WEAPON,weapon.getObjectType());
        assertEquals(SymbolType.WEAPON,weapon.getSymbolType());

        final Rod rod = new Rod(RodType.SLOW_MONSTER);
        assertEquals(ObjectType.ROD,rod.getObjectType());
        assertEquals(SymbolType.ROD,rod.getSymbolType());

        final int goldValue=100;
        final Gold gold = new Gold(goldValue);
        assertEquals(ObjectType.GOLD,gold.getObjectType());
        assertEquals(SymbolType.GOLD,gold.getSymbolType());
        assertEquals(goldValue,gold.getGoldValue());

        final Amulet amulet = new Amulet();
        assertEquals(ObjectType.AMULET,amulet.getObjectType());
        assertEquals(SymbolType.AMULET,amulet.getSymbolType());

    }
}
