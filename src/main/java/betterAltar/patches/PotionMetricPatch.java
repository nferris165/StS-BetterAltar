package betterAltar.patches;

import betterAltar.potions.AltarPotion;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.metrics.MetricData;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.ui.panels.PotionPopUp;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class PotionMetricPatch {
    @SpirePatch(
            clz = PotionPopUp.class,
            method = "updateInput"
    )

    public static class EventSwapPatch {
        @SpireInsertPatch(
                locator=Locator.class
        )
        public static void Insert(PotionPopUp __instance, AbstractPotion ___potion){
            if(___potion.ID.equals(AltarPotion.POTION_ID)){
                //BetterAltar.logger.info("got here\n\n");
                CardCrawlGame.metricData.potions_floor_usage.add(13000 + AbstractDungeon.floorNum);
            }
        }
    }

    public static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(MetricData.class, "potions_floor_usage");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
        }
    }
}
