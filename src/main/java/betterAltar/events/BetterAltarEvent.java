package betterAltar.events;

import betterAltar.BetterAltar;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.curses.Decay;
import com.megacrit.cardcrawl.core.AbstractCreature;
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
    private int hpLoss;
    private String optionsChosen;

    public BetterAltarEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        if (AbstractDungeon.player.hasRelic("Golden Idol")) {
            this.imageEventText.setDialogOption(OPTIONS[0], false, new BloodyIdol());
        } else {
            this.imageEventText.setDialogOption(OPTIONS[1], true, new BloodyIdol());
        }

        if (AbstractDungeon.ascensionLevel >= 15) {
            this.hpLoss = MathUtils.round((float)AbstractDungeon.player.maxHealth * 0.35F);
        } else {
            this.hpLoss = MathUtils.round((float)AbstractDungeon.player.maxHealth * 0.25F);
        }

        this.imageEventText.setDialogOption(OPTIONS[2] + 5 + OPTIONS[3] + this.hpLoss + OPTIONS[4]);
        this.imageEventText.setDialogOption(OPTIONS[6], CardLibrary.getCopy("Decay"));
    }

    @Override
    public void onEnterRoom() {
        if (Settings.AMBIANCE_ON) {
            CardCrawlGame.sound.play("EVENT_FORGOTTEN");
        }

    }
    protected void buttonEffect(int buttonPressed) {
        switch(this.screenNum) {// 74
            case 0:
                switch(buttonPressed) {// 77
                    case 0:
                        //metric logged in function
                        this.gainChalice();// 79
                        this.showProceedScreen(DIALOG_2);// 80
                        CardCrawlGame.sound.play("HEAL_1");// 81
                        return;// 108
                    case 1:
                        AbstractDungeon.player.increaseMaxHp(5, false);// 84
                        AbstractDungeon.player.damage(new DamageInfo((AbstractCreature)null, this.hpLoss));// 85
                        CardCrawlGame.sound.play("HEAL_3");// 86
                        this.showProceedScreen(DIALOG_3);// 87
                        logMetricDamageAndMaxHPGain("Forgotten Altar", "Shed Blood", this.hpLoss, 5);// 88
                        return;
                    case 2:
                        CardCrawlGame.sound.play("BLUNT_HEAVY");// 91
                        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.MED, true);// 92
                        AbstractCard curse = new Decay();// 93
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse, (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));// 94
                        this.showProceedScreen(DIALOG_4);// 96
                        logMetricObtainCard("Forgotten Altar", "Smashed Altar", curse);// 97
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
