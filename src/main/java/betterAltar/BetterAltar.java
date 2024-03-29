package betterAltar;

import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.ReflectionHacks;
import basemod.eventUtil.AddEventParams;
import basemod.eventUtil.EventUtils;
import basemod.helpers.RelicType;
import basemod.interfaces.*;
import betterAltar.events.BetterAltarEvent;
import betterAltar.potions.AltarPotion;
import betterAltar.relics.BloodRelic;
import betterAltar.util.TextureLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.audio.Sfx;
import com.megacrit.cardcrawl.audio.SoundMaster;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Properties;

@SuppressWarnings("unused")

@SpireInitializer
public class BetterAltar implements
        EditCardsSubscriber,
        EditRelicsSubscriber,
        EditStringsSubscriber,
        EditKeywordsSubscriber,
        EditCharactersSubscriber,
        PostInitializeSubscriber{

    public static final Logger logger = LogManager.getLogger(BetterAltar.class.getName());

    //mod settings
    public static Properties defaultSettings = new Properties();
    public static final String health_limit_settings = "noHealthLimit";
    public static boolean healthLimit = false;

    private static final String MODNAME = "Better Altar";
    private static final String AUTHOR = "Nichilas";
    private static final String DESCRIPTION = "A mod to make the Forgotten Altar Event better";

    private static final String BADGE_IMAGE = "betterAltarResources/images/Badge.png";

    private static final String AUDIO_PATH = "betterAltarResources/audio/";

    private static final String modID = "betterAltar";

    public static final Color POTION_RED = CardHelper.getColor(76, 25, 43);
    public static final Color POTION_ALT = CardHelper.getColor(211, 40, 2);
    public static final Color POTION_BOT = CardHelper.getColor(156, 4, 2);



    //Image Directories
    public static String makeCardPath(String resourcePath) {
        return modID + "Resources/images/cards/" + resourcePath;
    }

    public static String makeEventPath(String resourcePath) {
        return modID + "Resources/images/events/" + resourcePath;
    }

    public static String makeMonsterPath(String resourcePath) {
        return modID + "Resources/images/monsters/" + resourcePath;
    }

    public static String makeOrbPath(String resourcePath) {
        return modID + "Resources/images/orbs/" + resourcePath;
    }

    public static String makePowerPath(String resourcePath) {
        return modID + "Resources/images/powers/" + resourcePath;
    }

    public static String makeRelicPath(String resourcePath) {
        return modID + "Resources/images/relics/" + resourcePath;
    }

    public static String makeRelicOutlinePath(String resourcePath) {
        return modID + "Resources/images/relics/outline/" + resourcePath;
    }

    public static String makeUIPath(String resourcePath) {
        return modID + "Resources/images/ui/" + resourcePath;
    }

    public static String makeVfxPath(String resourcePath) {
        return modID + "Resources/images/vfx/" + resourcePath;
    }


    public BetterAltar() {
        BaseMod.subscribe(this);

        logger.info("Adding mod settings");
        defaultSettings.setProperty(health_limit_settings, "FALSE");
        try {
            SpireConfig config = new SpireConfig("betterAltar", "betterAltarConfig", defaultSettings);
            config.load();
            healthLimit = config.getBool(health_limit_settings);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initialize() {
        BetterAltar betterAltar = new BetterAltar();
    }

    public void receiveEditPotions() {
        BaseMod.addPotion(AltarPotion.class, POTION_ALT, POTION_BOT, POTION_RED, AltarPotion.POTION_ID, null);
    }

    @Override
    public void receiveEditCards() {

    }

    @Override
    public void receiveEditCharacters() {
        receiveEditPotions();
    }

    @Override
    public void receiveEditKeywords() {
        Gson gson = new Gson();
        String language = getLanguageString();

        String json = Gdx.files.internal(modID + "Resources/localization/" + language + "/Keyword-Strings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        com.evacipated.cardcrawl.mod.stslib.Keyword[] keywords = gson.fromJson(json, com.evacipated.cardcrawl.mod.stslib.Keyword[].class);

        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword(modID.toLowerCase(), keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
            }
        }
    }

    @Override
    public void receiveEditRelics() {
        BaseMod.addRelic(new BloodRelic(), RelicType.SHARED);
    }

    private static String getLanguageString() {
        switch (Settings.language) {
            case ZHS:
                return "zhs";
            default:
                return "eng";
        }
    }

    @Override
    public void receiveEditStrings() {
        // Get Localization
        String language = getLanguageString();

        BaseMod.loadCustomStringsFile(CardStrings.class,
                modID + "Resources/localization/" + language + "/Card-Strings.json");
        BaseMod.loadCustomStringsFile(CharacterStrings.class,
                modID + "Resources/localization/" + language + "/Character-Strings.json");
        BaseMod.loadCustomStringsFile(EventStrings.class,
                modID + "Resources/localization/" + language + "/Event-Strings.json");
        BaseMod.loadCustomStringsFile(MonsterStrings.class,
                modID + "Resources/localization/" + language + "/Monster-Strings.json");
        BaseMod.loadCustomStringsFile(OrbStrings.class,
                modID + "Resources/localization/" + language + "/Orb-Strings.json");
        BaseMod.loadCustomStringsFile(PotionStrings.class,
                modID + "Resources/localization/" + language + "/Potion-Strings.json");
        BaseMod.loadCustomStringsFile(PowerStrings.class,
                modID + "Resources/localization/" + language + "/Power-Strings.json");
        BaseMod.loadCustomStringsFile(RelicStrings.class,
                modID + "Resources/localization/" + language + "/Relic-Strings.json");
        BaseMod.loadCustomStringsFile(UIStrings.class,
                modID + "Resources/localization/" + language + "/UI-Strings.json");
    }

    private void loadAudio() {
        HashMap<String, Sfx> map = ReflectionHacks.getPrivate(CardCrawlGame.sound, SoundMaster.class, "map");
        //map.put("Pop", new Sfx(AUDIO_PATH + "pop.ogg", false));
    }

    public static String makeID(String idText) {
        return modID + ":" + idText;
    }

    @Override
    public void receivePostInitialize() {
        UIStrings configStrings = CardCrawlGame.languagePack.getUIString(makeID("ConfigMenuText"));
        Texture badgeTexture = TextureLoader.getTexture(BADGE_IMAGE);
        ModPanel settingsPanel = new ModPanel();

        ModLabeledToggleButton ascLimitButton = new ModLabeledToggleButton(configStrings.TEXT[0],
                350.0f, 750.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                healthLimit,
                settingsPanel,
                (label) -> {},
                (button) -> {

                    healthLimit = button.enabled;
                    try {
                        SpireConfig config = new SpireConfig("betterAltar", "betterAltarConfig", defaultSettings);
                        config.setBool(health_limit_settings, healthLimit);
                        config.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });


        settingsPanel.addUIElement(ascLimitButton);
        BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);

        //events
        if(healthLimit){
            BaseMod.addEvent(new AddEventParams.Builder(BetterAltarEvent.ID, BetterAltarEvent.class).dungeonID(TheCity.ID)
                    .eventType(EventUtils.EventType.NORMAL).create());
        }
        else{
            BaseMod.addEvent(new AddEventParams.Builder(BetterAltarEvent.ID, BetterAltarEvent.class).dungeonID(TheCity.ID)
                    .eventType(EventUtils.EventType.NORMAL).bonusCondition(
                            () -> {
                                if(AbstractDungeon.ascensionLevel >= 15){
                                    return AbstractDungeon.player.currentHealth >= AbstractDungeon.player.maxHealth * 0.35F;
                                } else{
                                    return AbstractDungeon.player.currentHealth >= AbstractDungeon.player.maxHealth * 0.4F;
                                }
                            }
                    ).create());
        }


        //audio
        loadAudio();
    }
}
