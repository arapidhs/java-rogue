package com.dungeoncode.javarogue.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Objects;

public class GameState {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameState.class);

    private final Config config;

    private GameEndReason gameEndReason;
    private int score;
    private int deathMonsterId;
    private int maxLevel;
    private int level;
    private KillType killType;

//    private final MessageSystem messageSystem;

//    private final Set<String> usedDescriptions = new HashSet<>();
//    private final Map<PotionType, String> potionColors = new HashMap<>();
//    private final Map<ScrollType, String> scrollNames = new HashMap<>();
//    private final Map<RingType, Stone> ringStones = new HashMap<>();
//    private final Map<SpellType, String> spellMaterials = new HashMap<>();
//    private final Map<SpellType, StickType> spellStickTypes = new HashMap<>();
//    private Player player;

    public GameState(@Nonnull final Config config) {
        Objects.requireNonNull(config);
        this.config = config;
    }

    public Config getConfig() {
        return config;
    }

    public GameEndReason getGameEndReason() {
        return gameEndReason;
    }

    public int getScore() {
        return score;
    }

    public int getDeathMonsterId() {
        return deathMonsterId;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getLevel() {
        return level;
    }

    public KillType getKillType() {
        return killType;
    }

    //    public void init() {
//        usedDescriptions.clear();
//        assignPotionColors();
//        generateScrollNames();
//        assignRingStones();
//        assignStickMaterials();
//        initProbabilities();
//        initPlayer();
//    }

//    private void initPlayer() {
//        player = new Player( this,"name", config.getMaxPack(), config.getInitPlayerStats(), config.getHungerTime());
//        Food food = new Food();
//        player.addToInventory(food, true);
//    }
//
//    private void assignPotionColors() {
//        List<String> shuffledColors = new ArrayList<>(Registry.RAINBOW_COLORS);
//        Collections.shuffle(shuffledColors);
//        int index = 0;
//        for (PotionType potion : PotionType.values()) {
//            String color = shuffledColors.get(index++);
//            potionColors.put(potion, color);
//            usedDescriptions.add(color);
//        }
//    }
//
//    private void generateScrollNames() {
//        List<String> syllables = new ArrayList<>(Registry.SCROLL_SYLLABLES);
//        Collections.shuffle(syllables);
//        for (ScrollType scroll : ScrollType.values()) {
//            StringBuilder name = new StringBuilder();
//            int wordCount = RogueUtils.rnd(3) + 2; // 2–4 words
//            while (wordCount-- > 0) {
//                int syllableCount = RogueUtils.rnd(3) + 1; // 1–3 syllables
//                while (syllableCount-- > 0) {
//                    int index = RogueUtils.rnd(syllables.size());
//                    String syllable = syllables.get(index);
//                    if (name.length() + syllable.length() > 40) {
//                        break;
//                    }
//                    name.append(syllable);
//                }
//                if (wordCount > 0) {
//                    name.append(" ");
//                }
//            }
//            String finalName = name.toString().trim();
//            scrollNames.put(scroll, finalName);
//            usedDescriptions.add(finalName);
//        }
//    }
//
//    private void assignRingStones() {
//        List<Stone> shuffledStones = new ArrayList<>(Registry.RING_STONES);
//        Collections.shuffle(shuffledStones);
//        int index = 0;
//        for (RingType ring : RingType.values()) {
//            Stone stone = shuffledStones.get(index++);
//            ringStones.put(ring, stone);
//            usedDescriptions.add(stone.name());
//            ItemInfo currentInfo = Registry.getInstance().getRingInfo(ring);
//            ItemInfo updatedInfo = new ItemInfo(
//                    currentInfo.name(),
//                    currentInfo.probability(),
//                    currentInfo.worth() + stone.value(),
//                    currentInfo.guess(),
//                    currentInfo.isKnown()
//            );
//            Registry.getInstance().updateRingInfo(ring, updatedInfo);
//        }
//    }
//
//    private void assignStickMaterials() {
//        List<String> woods = new ArrayList<>(Registry.STICK_WOODS);
//        List<String> metals = new ArrayList<>(Registry.STICK_METALS);
//        Set<String> usedMetals = new HashSet<>();
//        Collections.shuffle(woods);
//        Collections.shuffle(metals);
//        int woodIndex = 0;
//        int metalIndex = 0;
//
//        for (SpellType spell : SpellType.values()) {
//            boolean useMetal = RogueUtils.rnd(2) == 0;
//            String material;
//            StickType stickType;
//
//            if (useMetal && metalIndex < metals.size() && !usedMetals.contains(metals.get(metalIndex))) {
//                material = metals.get(metalIndex++);
//                stickType = StickType.WAND;
//                usedMetals.add(material);
//            } else if (woodIndex < woods.size()) {
//                material = woods.get(woodIndex++);
//                stickType = StickType.STAFF;
//            } else {
//                throw new IllegalArgumentException(
//                        String.format("No available materials for SpellType %s", spell));
//            }
//
//            spellMaterials.put(spell, material);
//            spellStickTypes.put(spell, stickType);
//            usedDescriptions.add(material);
//        }
//    }
//
//    private <T> void computeCumulativeProbabilities(Map<T, ItemInfo> infoMap) {
//        List<Map.Entry<T, ItemInfo>> entries = new ArrayList<>(infoMap.entrySet());
//        for (int i = 1; i < entries.size(); i++) {
//            ItemInfo prev = entries.get(i - 1).getValue();
//            ItemInfo curr = entries.get(i).getValue();
//            ItemInfo updated = new ItemInfo(
//                    curr.name(),
//                    curr.probability() + prev.probability(),
//                    curr.worth(),
//                    curr.guess(),
//                    curr.isKnown()
//            );
//            infoMap.put(entries.get(i).getKey(), updated);
//        }
//    }
//
//    private void initProbabilities() {
//        Registry registry = Registry.getInstance();
//        computeCumulativeProbabilities(registry.getItemTypeInfo());
//        computeCumulativeProbabilities(registry.getPotionInfo());
//        computeCumulativeProbabilities(registry.getScrollInfo());
//        computeCumulativeProbabilities(registry.getRingInfo());
//        computeCumulativeProbabilities(registry.getSpellTypeInfo());
//        computeCumulativeProbabilities(registry.getWeaponInfo());
//        computeCumulativeProbabilities(registry.getArmorInfo());
//        if ( config.isDebug() ) {
//            checkProbabilities(registry.getItemTypeInfo());
//            checkProbabilities(registry.getPotionInfo());
//            checkProbabilities(registry.getScrollInfo());
//            checkProbabilities(registry.getRingInfo());
//            checkProbabilities(registry.getSpellTypeInfo());
//            checkProbabilities(registry.getWeaponInfo());
//            checkProbabilities(registry.getArmorInfo());
//        }
//    }
//
//    private void checkProbabilities(Map<?, ItemInfo> infoMap) {
//        if (infoMap.isEmpty()) {
//            return;
//        }
//        List<Map.Entry<?, ItemInfo>> entries = new ArrayList<>(infoMap.entrySet());
//        ItemInfo last = entries.get(entries.size() - 1).getValue();
//        if (last.probability() == 100) {
//            return;
//        }
//        String mapName = infoMap.isEmpty() ? "Unknown" : entries.get(0).getKey().getClass().getSimpleName();
//        String msg = String.format(
//                "Bad percentages for %s (size = %d):",
//                mapName,
//                entries.size());
//        LOGGER.debug(msg);
//        for (Map.Entry<?, ItemInfo> entry : entries) {
//            ItemInfo info = entry.getValue();
//            LOGGER.debug(String.format("%3d%% %s", info.probability(), info.name()));
//        }
//        throw new IllegalStateException(msg);
//    }


//    public MessageSystem getMessageSystem() {
//        return messageSystem;
//    }

}