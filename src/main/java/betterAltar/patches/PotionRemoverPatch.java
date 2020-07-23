package betterAltar.patches;

import betterAltar.potions.AltarPotion;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.random.Random;

public class PotionRemoverPatch {
    @SpirePatch(
            clz = PotionHelper.class,
            method = "getRandomPotion",
            paramtypez = {
                    Random.class
            }
    )

    public static class RandomPatch {

        public static AbstractPotion Postfix(AbstractPotion __result, Random rng){
            if (__result.ID.equals(AltarPotion.POTION_ID)) {
                return PotionHelper.getRandomPotion(rng);
            }
            return __result;
        }
    }

    @SpirePatch(
            clz = PotionHelper.class,
            method = "getRandomPotion",
            paramtypez = {}
    )

    public static class Random2Patch {

        public static AbstractPotion Postfix(AbstractPotion __result){
            if (__result.ID.equals(AltarPotion.POTION_ID)) {
                return PotionHelper.getRandomPotion();
            }
            return __result;
        }
    }
}
