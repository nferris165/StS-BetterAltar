package betterAltar.patches;

import basemod.ReflectionHacks;
import betterAltar.relics.BloodRelic;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.colorless.Bite;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.Vampires;
import com.megacrit.cardcrawl.relics.BloodVial;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import static betterAltar.BetterAltar.makeID;

public class VampiresPatch {
    private static String[] DESCRIPTIONS = CardCrawlGame.languagePack.getEventString(makeID("Vampires")).DESCRIPTIONS;
    private static String[] OPTIONS = CardCrawlGame.languagePack.getEventString(makeID("Vampires")).OPTIONS;

    @SpirePatch(
            clz = Vampires.class,
            method = SpirePatch.CONSTRUCTOR
    )

    public static class OptionPatch {
        @SpireInsertPatch(
                locator=Locator.class
        )
        public static void Insert(Vampires __instance, boolean ___hasVial){
            if (AbstractDungeon.player.hasRelic(BloodRelic.ID) && !___hasVial && __instance.imageEventText.optionList.size() == 1) {
                String vialName = (new BloodRelic()).name;
                __instance.imageEventText.setDialogOption(OPTIONS[3] + vialName + OPTIONS[4], new Bite());
            }
        }
    }

    @SpirePatch(
            clz = Vampires.class,
            method = "buttonEffect"
    )

    public static class FunctionPatch {
        @SpireInsertPatch(
                locator=Locator.class
        )
        public static void Insert(Vampires __instance, int buttonPressed, boolean ___hasVial){
            if (AbstractDungeon.player.hasRelic(BloodRelic.ID) && !___hasVial) {
                ReflectionHacks.setPrivate(__instance, Vampires.class,"hasVial", true);
                AbstractDungeon.player.loseRelic(BloodRelic.ID);
            }
        }

        @SpireInsertPatch(
                locator=TextLocator.class
        )
        public static void Insert2(Vampires __instance, int buttonPressed, boolean ___hasVial){
            if (!AbstractDungeon.player.hasRelic(BloodVial.ID)) {
                __instance.imageEventText.updateBodyText(DESCRIPTIONS[0]);
            }
        }
    }



    public static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(Vampires.class, "hasVial");
            return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
        }
    }

    public static class TextLocator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "loseRelic");
            return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
