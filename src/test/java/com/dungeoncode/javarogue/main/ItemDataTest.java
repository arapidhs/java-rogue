package com.dungeoncode.javarogue.main;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class ItemDataTest {

    @Test
    void initializeItemDataTest(){
        final Config config = new Config();
        final RogueRandom rogueRandom = new RogueRandom(config.getSeed());
        final ItemData itemData = new ItemData(rogueRandom,config.getMaxScrollGeneratedNameLength());

        itemData.init();

        Arrays.stream(ScrollType.values())
                .forEach(scrollType -> assertNotNull(itemData.getName(scrollType)));
        Arrays.stream(PotionType.values())
                .forEach(potionType -> assertNotNull(itemData.getName(potionType)));
        Arrays.stream(RingType.values())
                .forEach(ringType -> assertNotNull(itemData.getName(ringType)));

        Arrays.stream(RodType.values())
                .forEach(rodType -> assertNotNull(itemData.getRodForm(rodType)));

        final RingInfoTemplate adornmentRingTemplate = Templates.findTemplateBySubType(RingInfoTemplate.class, RingType.ADORNMENT);
        final int ringTemplateValue = adornmentRingTemplate.getWorth();
        assertTrue(ringTemplateValue>0);

        final int ringStoneWorth = itemData.getRingWorth(RingType.ADORNMENT);
        assertTrue(ringStoneWorth>ringTemplateValue);

    }
}
