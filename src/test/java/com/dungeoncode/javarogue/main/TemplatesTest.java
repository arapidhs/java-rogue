package com.dungeoncode.javarogue.main;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

public class TemplatesTest {

    @Test
    void testTemplateEquality() {
        final TestTemplate template1 = new TestTemplate(1);
        final TestTemplate template2 = new TestTemplate(1);
        final TestTemplate template3 = new TestTemplate(2);
        assertEquals(template1, template2);
        assertNotEquals(template1, template3);
    }

    @Test
    void testMonstersLoading() {

        assertEquals(26, Templates.getTemplates(MonsterTemplate.class).size());

        final long griffinId = 7;
        final String griffinName = "griffin";
        final int griffinExperience = 2000;
        final EnumSet<StatusEffect> griffinStatusEffects = EnumSet.of(
                StatusEffect.IS_MEAN,
                StatusEffect.IS_FLYING,
                StatusEffect.IS_REGENERATING
                                                                     );
        final MonsterTemplate griffinTemplate = Templates.getTemplate(MonsterTemplate.class, griffinId);
        assertNotNull(griffinTemplate);
        assertEquals(griffinId, griffinTemplate.getId());
        assertEquals(griffinName, griffinTemplate.getName());
        assertEquals(griffinExperience, griffinTemplate.getStats().getExperience());
        assertEquals(griffinStatusEffects, griffinTemplate.getStatusEffects());
    }

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

    private static class TestTemplate extends AbstractTemplate {
        public TestTemplate(final long id) {
            super(id);
        }
    }

}
