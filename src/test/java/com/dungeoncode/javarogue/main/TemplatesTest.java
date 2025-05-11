package com.dungeoncode.javarogue.main;

import com.dungeoncode.javarogue.entity.creature.CreatureFlag;
import com.dungeoncode.javarogue.entity.item.ItemFlag;
import com.dungeoncode.javarogue.entity.item.ObjectType;
import com.dungeoncode.javarogue.entity.item.armor.Armor;
import com.dungeoncode.javarogue.entity.item.armor.ArmorInfoTemplate;
import com.dungeoncode.javarogue.entity.item.armor.ArmorType;
import com.dungeoncode.javarogue.entity.item.potion.PotionInfoTemplate;
import com.dungeoncode.javarogue.entity.item.potion.PotionType;
import com.dungeoncode.javarogue.entity.item.ring.RingInfoTemplate;
import com.dungeoncode.javarogue.entity.item.ring.RingType;
import com.dungeoncode.javarogue.entity.item.rod.RodInfoTemplate;
import com.dungeoncode.javarogue.entity.item.rod.RodType;
import com.dungeoncode.javarogue.entity.item.scroll.ScrollInfoTemplate;
import com.dungeoncode.javarogue.entity.item.scroll.ScrollType;
import com.dungeoncode.javarogue.entity.item.weapon.Weapon;
import com.dungeoncode.javarogue.entity.item.weapon.WeaponInfoTemplate;
import com.dungeoncode.javarogue.entity.item.weapon.WeaponType;
import com.dungeoncode.javarogue.system.combat.KillType;
import com.dungeoncode.javarogue.template.*;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for verifying correct loading, parsing, and consistency of game templates.
 */
public class TemplatesTest {

    /**
     * Verifies that templates with the same ID are considered equal,
     * and templates with different IDs are not.
     */
    @Test
    void testTemplateEquality() {
        final TestTemplate template1 = new TestTemplate(1);
        final TestTemplate template2 = new TestTemplate(1);
        final TestTemplate template3 = new TestTemplate(2);
        assertEquals(template1, template2);
        assertNotEquals(template1, template3);
    }

    /**
     * Verifies that Monster templates are loaded correctly and
     * a specific monster (griffin) matches expected properties.
     */
    @Test
    void testMonstersLoading() {
        assertEquals(26, Templates.getTemplates(MonsterTemplate.class).size());

        final long griffinId = 7;
        final String griffinName = "griffin";
        final int griffinExperience = 2000;
        final EnumSet<CreatureFlag> griffinCreatureFlags = EnumSet.of(
                CreatureFlag.ISMEAN,
                CreatureFlag.ISFLY,
                CreatureFlag.ISREGEN
        );

        final MonsterTemplate griffinTemplate = Templates.getTemplate(MonsterTemplate.class, griffinId);
        assertNotNull(griffinTemplate);
        assertEquals(griffinId, griffinTemplate.getId());
        assertEquals(griffinName, griffinTemplate.getName());
        assertEquals(griffinExperience, griffinTemplate.getStats().getExperience());
       assertEquals(griffinCreatureFlags, griffinTemplate.getCreatureFlags());
    }

    /**
     * Verifies that KillType templates are loaded correctly and
     * a specific kill type (arrow) matches expected properties.
     */
    @Test
    void testKillTypesLoading() {
        assertEquals(5, Templates.getTemplates(KillTypeTemplate.class).size());

        final long arrowId = 1;
        final KillType arrowKillType = KillType.ARROW;
        final String arrowName = "arrow";

        final KillTypeTemplate arrowTemplate = Templates.getTemplate(KillTypeTemplate.class, arrowId);
        assertNotNull(arrowTemplate);
        assertEquals(arrowId, arrowTemplate.getId());
        assertEquals(arrowKillType, arrowTemplate.getKillType());
        assertEquals(arrowName, arrowTemplate.getName());
    }

    /**
     * Verifies that the DragonBreathTemplate is loaded correctly.
     */
    @Test
    void testDragonBreathTemplateLoading() {
        final Set<DragonBreathTemplate> templates = Templates.getTemplates(DragonBreathTemplate.class);
        assertEquals(1, templates.size());

        final DragonBreathTemplate flameTemplate = Templates.getTemplate(DragonBreathTemplate.class, 1);
        assertNotNull(flameTemplate);
        assertEquals(1, flameTemplate.getId());
        assertEquals(DragonBreathType.FLAME, flameTemplate.getBreathType());
    }


    /**
     * Verifies that ObjectInfo templates are loaded correctly and
     * specific objects (scroll and rod) match expected probability and cumulative probability values.
     */
    @Test
    void testObjectInfoLoading() {
        assertEquals(9, Templates.getTemplates(ObjectInfoTemplate.class).size());

        // Verify scroll template
        final long scrollId = 2;
        final ObjectType scrollObjectType = ObjectType.SCROLL;
        final double scrollProbability = 36;
        final double scrollCumulativeProbability = 62;

        final ObjectInfoTemplate scrollTemplate = Templates.getTemplate(ObjectInfoTemplate.class, scrollId);
        assertNotNull(scrollTemplate);
        assertEquals(scrollId, scrollTemplate.getId());
        assertEquals(scrollObjectType, scrollTemplate.getObjectType());
        assertEquals(scrollProbability, scrollTemplate.getProbability());
        assertEquals(scrollCumulativeProbability, scrollTemplate.getCumulativeProbability());
        assertTrue(scrollTemplate.isStackable());

        // Verify rod template
        final long rodId = 7;
        final ObjectType rodObjectType = ObjectType.ROD;
        final double rodProbability = 4;
        final double rodCumulativeProbability = 100;

        final ObjectInfoTemplate rodTemplate = Templates.getTemplate(ObjectInfoTemplate.class, rodId);
        assertNotNull(rodTemplate);
        assertEquals(rodId, rodTemplate.getId());
        assertEquals(rodObjectType, rodTemplate.getObjectType());
        assertEquals(rodProbability, rodTemplate.getProbability());
        assertEquals(rodCumulativeProbability, rodTemplate.getCumulativeProbability());
        assertFalse(rodTemplate.isStackable());
    }

    /**
     * Verifies that ArmorInfo templates are loaded correctly and
     * specific objects (splint mail) match expected probability and cumulative probability values.
     */
    @Test
    void testArmorInfoLoading() {
        assertEquals(8, Templates.getTemplates(ArmorInfoTemplate.class).size());

        // Verify scroll template
        final long splintMailId = 6;
        final ArmorType splintMailArmorType = ArmorType.SPLINT_MAIL;
        final String splintMailName = "splint mail";
        final int splintMailWorth = 80;
        final int splintMailArmorClass = 4;
        final double splitMailProbability = 10;
        final double splintMailCumulativeProbability = 85;

        final ArmorInfoTemplate splintMailTemplate = Templates.getTemplate(ArmorInfoTemplate.class, splintMailId);
        assertNotNull(splintMailTemplate);
        assertEquals(splintMailId, splintMailTemplate.getId());
        assertEquals(splintMailArmorType, splintMailTemplate.getArmorType());
        assertEquals(splintMailName, splintMailTemplate.getName());
        assertEquals(splintMailArmorClass, splintMailTemplate.getArmorClass());
        assertEquals(splintMailWorth, splintMailTemplate.getWorth());
        assertEquals(splitMailProbability, splintMailTemplate.getProbability());
        assertEquals(splintMailCumulativeProbability, splintMailTemplate.getCumulativeProbability());

        final Armor splintMail = new Armor(ArmorType.SPLINT_MAIL);
        assertEquals(splintMailArmorClass, splintMail.getArmorClass());
    }

    /**
     * Verifies that PotionInfo templates are loaded correctly and
     * specific objects (extra healing) match expected probability and cumulative probability values.
     */
    @Test
    void testPotionInfoLoading() {
        assertEquals(14, Templates.getTemplates(PotionInfoTemplate.class).size());

        final long extraHealingId = 10;
        final PotionType extraHealingPotionType = PotionType.EXTRA_HEALING;
        final String extraHealingName = "extra healing";
        final int extraHealingWorth = 200;
        final double extraHealingProbability = 5;
        final double extraHealingCumulativeProbability = 71;

        final PotionInfoTemplate extraHealingTemplate = Templates.getTemplate(PotionInfoTemplate.class, extraHealingId);
        assertNotNull(extraHealingTemplate);
        assertEquals(extraHealingId, extraHealingTemplate.getId());
        assertEquals(extraHealingPotionType, extraHealingTemplate.getPotionType());
        assertEquals(extraHealingName, extraHealingTemplate.getName());
        assertEquals(extraHealingWorth, extraHealingTemplate.getWorth());
        assertEquals(extraHealingProbability, extraHealingTemplate.getProbability());
        assertEquals(extraHealingCumulativeProbability, extraHealingTemplate.getCumulativeProbability());
    }

    /**
     * Verifies that ScrollInfo templates are loaded correctly and
     * specific objects (enchant weapon) match expected probability and cumulative probability values.
     */
    @Test
    void testScrollInfoLoading() {
        assertEquals(18, Templates.getTemplates(ScrollInfoTemplate.class).size());

        final long enchantWeaponId = 14;
        final ScrollType enchantWeaponType = ScrollType.ENCHANT_WEAPON;
        final String enchantWeaponName = "enchant weapon";
        final int enchantWeaponWorth = 150;
        final double enchantWeaponProbability = 8;
        final double enchantWeaponCumulativeProbability = 84;

        final ScrollInfoTemplate enchantWeaponTemplate = Templates.getTemplate(ScrollInfoTemplate.class, enchantWeaponId);
        assertNotNull(enchantWeaponTemplate);
        assertEquals(enchantWeaponId, enchantWeaponTemplate.getId());
        assertEquals(enchantWeaponType, enchantWeaponTemplate.getScrollType());
        assertEquals(enchantWeaponName, enchantWeaponTemplate.getName());
        assertEquals(enchantWeaponWorth, enchantWeaponTemplate.getWorth());
        assertEquals(enchantWeaponProbability, enchantWeaponTemplate.getProbability());
        assertEquals(enchantWeaponCumulativeProbability, enchantWeaponTemplate.getCumulativeProbability());
    }

    /**
     * Verifies that WeaponInfo templates are loaded correctly and
     * specific objects (two-handed sword) match expected probability and cumulative probability values.
     */
    @Test
    void testWeaponInfoLoading() {
        assertEquals(9, Templates.getTemplates(WeaponInfoTemplate.class).size());

        final long twoHandedId = 6;
        final WeaponType type = WeaponType.TWO_HANDED_SWORD;
        final int worth = 75;
        final String name = "two handed sword";
        final double probability = 10;
        final double cumulative = 64;

        final WeaponInfoTemplate template = Templates.getTemplate(WeaponInfoTemplate.class, twoHandedId);
        assertNotNull(template);
        assertEquals(twoHandedId, template.getId());
        assertEquals(type, template.getWeaponType());
        assertEquals(name, template.getName());
        assertEquals(worth, template.getWorth());
        assertEquals(probability, template.getProbability());
        assertEquals(cumulative, template.getCumulativeProbability());

        final Weapon twoHandedSword = new Weapon(WeaponType.TWO_HANDED_SWORD);
        final String twoHandedSwordWieldDamage = "4x4";
        final String twoHandedSwordThrowDamage = "1x2";
        assertEquals(twoHandedSwordWieldDamage, twoHandedSword.getWieldDamage());
        assertEquals(twoHandedSwordThrowDamage, twoHandedSword.getThrowDamage());

        final Weapon arrow = new Weapon(WeaponType.ARROW);
        final String arrowWieldDamage = "1x1";
        final String arrowThrowDamage = "2x3";
        final WeaponType arrowLaunchWeapon = WeaponType.SHORT_BOW;
        final EnumSet<ItemFlag> arrowFlags = EnumSet.of(ItemFlag.ISMISL, ItemFlag.ISMANY);
        assertEquals(arrowWieldDamage, arrow.getWieldDamage());
        assertEquals(arrowThrowDamage, arrow.getThrowDamage());
        assertEquals(arrowLaunchWeapon, arrow.getLaunchWeapon());
        arrowFlags.forEach(flag -> assertTrue(arrow.getItemFlags().contains(flag)));
    }

    /**
     * Verifies that RodInfo templates are loaded correctly and
     * specific objects (slow monster) match expected probability and cumulative probability values.
     */
    @Test
    void testRodInfoLoading() {
        assertEquals(14, Templates.getTemplates(RodInfoTemplate.class).size());

        final long slowMonsterId = 9;
        final RodType type = RodType.SLOW_MONSTER;
        final String name = "slow monster";
        final int worth = 350;
        final double probability = 11;
        final double cumulative = 73;

        final RodInfoTemplate template = Templates.getTemplate(RodInfoTemplate.class, slowMonsterId);
        assertNotNull(template);
        assertEquals(slowMonsterId, template.getId());
        assertEquals(type, template.getRodType());
        assertEquals(name, template.getName());
        assertEquals(worth, template.getWorth());
        assertEquals(probability, template.getProbability());
        assertEquals(cumulative, template.getCumulativeProbability());
    }

    /**
     * Verifies that RingInfo templates are loaded correctly and
     * specific objects (dexterity) match expected probability and cumulative probability values.
     */
    @Test
    void testRingInfoLoading() {
        assertEquals(14, Templates.getTemplates(RingInfoTemplate.class).size());

        final long dexId = 8;
        final RingType dexType = RingType.DEXTERITY;
        final String dexName = "dexterity";
        final int dexWorth = 440;
        final double dexProbability = 8;
        final double dexCumulative = 62;

        final RingInfoTemplate template = Templates.getTemplate(RingInfoTemplate.class, dexId);
        assertNotNull(template);
        assertEquals(dexId, template.getId());
        assertEquals(dexType, template.getRingType());
        assertEquals(dexName, template.getName());
        assertEquals(dexWorth, template.getWorth());
        assertEquals(dexProbability, template.getProbability());
        assertEquals(dexCumulative, template.getCumulativeProbability());
    }


    /**
     * Verifies that cumulative probability handling works correctly:
     * <ul>
     * <li>The sum of individual probabilities is exactly 100.</li>
     * <li>The last item's cumulative probability is exactly 100.</li>
     * </ul>
     */
    @Test
    public void verifyCumulativeProbabilities() {
        final Optional<Templates.BadTemplateInfo> badTemplateInfo = Templates.verifyProbabilities();
        assertFalse(badTemplateInfo.isPresent());
    }

    @Test
    public void findTemplateByObjectTypeTest() {
        final ObjectInfoTemplate templateByObjectType = Templates.findTemplateByObjectType(ObjectType.RING);
        assert templateByObjectType != null;
        assertEquals("ring", templateByObjectType.getName());
    }

    /**
     * Simple concrete Template subclass for equality tests.
     */
    private static class TestTemplate extends AbstractTemplate {
        public TestTemplate(final long id) {
            super(id);
        }
    }
}
