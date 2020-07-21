package betterAltar.patches;

import betterAltar.events.BetterAltarEvent;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.city.ForgottenAltar;
import com.megacrit.cardcrawl.events.city.KnowingSkull;
import com.megacrit.cardcrawl.helpers.EventHelper;

public class EventHelperPatch {
    @SpirePatch(
            clz = EventHelper.class,
            method = "getEvent"
    )

    public static class EventSwapPatch {
        public static AbstractEvent Postfix(AbstractEvent __result, String key){

            if (__result instanceof ForgottenAltar) {

                return new BetterAltarEvent();
            }
            return __result;
        }
    }
}