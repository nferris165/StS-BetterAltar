package betterAltar.events;

import betterAltar.BetterAltar;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.curses.Decay;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.BloodyIdol;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.relics.GoldenIdol;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

public class BetterAltarEvent extends AbstractImageEvent {

    public static final String ID = BetterAltar.makeID("BetterAltar");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String IMG = "images/events/forgottenAltar.jpg";

    private static final String DIALOG_2;
    private static final String DIALOG_3;
    private static final String DIALOG_4;
    private int maxHpLoss, hpGain, hpLoss;
    private String optionsChosen;

    public BetterAltarEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        if (AbstractDungeon.ascensionLevel >= 15) {
            this.maxHpLoss = MathUtils.round((float)AbstractDungeon.player.maxHealth * 0.12F);
            this.hpLoss = MathUtils.round((float)AbstractDungeon.player.maxHealth * 0.08F);
        } else {
            this.maxHpLoss = MathUtils.round((float)AbstractDungeon.player.maxHealth * 0.08F);
            this.hpLoss = MathUtils.round((float)AbstractDungeon.player.maxHealth * 0.05F);
        }

        if (AbstractDungeon.player.hasRelic("Golden Idol")) {
            this.imageEventText.setDialogOption(OPTIONS[0] + this.hpLoss + OPTIONS[5], false, new BloodyIdol());
        } else {
            this.imageEventText.setDialogOption(OPTIONS[1], true, new BloodyIdol());
        }

        this.imageEventText.setDialogOption(OPTIONS[2] + this.hpGain + OPTIONS[3] + this.maxHpLoss + OPTIONS[4]);
        this.imageEventText.setDialogOption(OPTIONS[6], CardLibrary.getCopy("Decay"), new BloodyIdol());
    }

    @Override
    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_FORGOTTEN");
        }

    }
    protected void buttonEffect(int buttonPressed) {
        switch(this.screenNum) {
            case 0:
                switch(buttonPressed) {
                    case 0:
                        //metric logged in function
                        this.gainChalice();
                        this.showProceedScreen(DIALOG_2);
                        AbstractDungeon.player.damage(new DamageInfo(null, this.hpLoss));
                        CardCrawlGame.sound.play("HEAL_1");
                        return;
                    case 1:
                        AbstractDungeon.player.decreaseMaxHealth(this.maxHpLoss);
                        AbstractDungeon.player.heal(this.hpGain, true);
                        CardCrawlGame.sound.play("HEAL_3");
                        this.showProceedScreen(DIALOG_3);
                        logMetricDamageAndMaxHPGain(ID, "Sacrifice", this.hpLoss, 5);
                        return;
                    case 2:
                        CardCrawlGame.sound.play("BLUNT_HEAVY");
                        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.MED, true);
                        AbstractCard curse = new Decay();
                        AbstractRelic relic = new BloodyIdol();
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
                        AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2),
                                (float)(Settings.HEIGHT / 2), relic);
                        this.showProceedScreen(DIALOG_4);
                        logMetricObtainCard(ID, "Desecrate", curse);
                        logMetricObtainCardAndRelic(ID, "Desecrate", curse, relic);
                        return;
                    default:
                        return;
                }
            default:
                this.openMap();// 105
        }
    }

    public void gainChalice() {
        int relicAtIndex = 0;

        for(int i = 0; i < AbstractDungeon.player.relics.size(); ++i) {
            if ((AbstractDungeon.player.relics.get(i)).relicId.equals("Golden Idol")) {
                relicAtIndex = i;
                break;
            }
        }

        if (AbstractDungeon.player.hasRelic("Bloody Idol")) {
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2),
                    (float)(Settings.HEIGHT / 2), RelicLibrary.getRelic("Circlet").makeCopy());
            logMetricRelicSwap(ID, "Gave Idol", new Circlet(), new GoldenIdol());
        } else {
            (AbstractDungeon.player.relics.get(relicAtIndex)).onUnequip();
            AbstractRelic bloodyIdol = RelicLibrary.getRelic("Bloody Idol").makeCopy();
            bloodyIdol.instantObtain(AbstractDungeon.player, relicAtIndex, false);
            logMetricRelicSwap(ID, "Gave Idol", new BloodyIdol(), new GoldenIdol());
        }

    }

    static {
        DIALOG_2 = DESCRIPTIONS[1];
        DIALOG_3 = DESCRIPTIONS[2];
        DIALOG_4 = DESCRIPTIONS[3];
    }
}
