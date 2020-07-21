package betterAltar.potions;

import betterAltar.BetterAltar;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.shrines.WeMeetAgain;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.RegenPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

public class AltarPotion extends AbstractPotion {


    public static final String POTION_ID = BetterAltar.makeID("AltarPotion");
    private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString(POTION_ID);

    public static final String NAME = potionStrings.NAME;
    public static final String[] DESCRIPTIONS = potionStrings.DESCRIPTIONS;

    private int regen;

    public AltarPotion() {
        super(NAME, POTION_ID, PotionRarity.PLACEHOLDER, PotionSize.HEART, PotionColor.POWER);

        isThrown = false;
    }

    @Override
    public void use(AbstractCreature target) {
        if (AbstractDungeon.getCurrRoom().phase == RoomPhase.COMBAT) {
            this.addToBot(new HealAction(AbstractDungeon.player, AbstractDungeon.player, (int)((float)AbstractDungeon.player.maxHealth * ((float)this.potency / 100.0F))));
            this.addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new RegenPower(AbstractDungeon.player, this.regen), this.regen));
        }
    }

    @Override
    public void initializeData() {
        this.potency = this.getPotency();

        this.description = DESCRIPTIONS[0] + potency + DESCRIPTIONS[1] + regen + DESCRIPTIONS[2];

        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
    }

    @Override
    public AbstractPotion makeCopy() {
        return new AltarPotion();
    }

    @Override
    public int getPotency(final int ascension) {
        int pot = 15;
        this.regen = pot / 3;
        return pot;
    }
}
