package betterAltar.patches;

import betterAltar.BetterAltar;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.ForgottenAltar;
import com.megacrit.cardcrawl.events.city.KnowingSkull;

@SpirePatch(
        clz=AbstractDungeon.class,
        method="initializeCardPools"
)
public class RemoveEventPatch {

    public static void Prefix(AbstractDungeon dungeon_instance) {
        AbstractDungeon.eventList.remove(ForgottenAltar.ID);
        BetterAltar.logger.info("Removing base Forgotten Altar event.");
    }
}
