package betterAltar.events;

import betterAltar.BetterAltar;
import betterAltar.potions.AltarPotion;
import betterAltar.relics.BloodRelic;
import betterAltar.util.AbstractEventDialog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.curses.Decay;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.vfx.ObtainPotionEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.scene.EventBgParticle;

import java.util.ArrayList;

public class BetterAltarEvent extends AbstractImageEvent {

    public static final String ID = BetterAltar.makeID("BetterAltar");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String IMG = "images/events/forgottenAltar.jpg";
    public AbstractEventDialog EventText = new AbstractEventDialog();

    private static final String DIALOG_2;
    private static final String DIALOG_2_ALT;
    private static final String DIALOG_3;
    private static final String DIALOG_3_ALT;
    private static final String DIALOG_4;
    private static final String DIALOG_5;
    private static final String DIALOG_5_ALT;
    private int hpLoss1, hpLoss2, hpLoss3, hpLossIdol, hpLossVial;
    private boolean curse, idol, vial;
    private int damageTaken;
    private ArrayList<AbstractCard> cardsGained;
    private String optionsChosen;

    public BetterAltarEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);
        this.EventText.loadImage(IMG);

        this.curse = true;
        this.damageTaken = 0;
        this.hpLossIdol = 1;
        this.hpLossVial = MathUtils.round((float)AbstractDungeon.player.maxHealth * 0.05F);
        this.idol = AbstractDungeon.player.hasRelic(GoldenIdol.ID);
        this.vial = AbstractDungeon.player.hasRelic(BloodVial.ID);
        this.optionsChosen = "";
        this.cardsGained = new ArrayList<>();

        if(AbstractDungeon.ascensionLevel >= 15) {
            this.hpLoss1 = MathUtils.round((float)AbstractDungeon.player.maxHealth * 0.50F);
            this.hpLoss2 = MathUtils.round((float)AbstractDungeon.player.maxHealth * 0.30F);
            this.hpLoss3 = MathUtils.round((float)AbstractDungeon.player.maxHealth * 0.35F);
        } else {
            this.hpLoss1 = MathUtils.round((float)AbstractDungeon.player.maxHealth * 0.45F);
            this.hpLoss2 = MathUtils.round((float)AbstractDungeon.player.maxHealth * 0.25F);
            this.hpLoss3 = MathUtils.round((float)AbstractDungeon.player.maxHealth * 0.30F);
        }

        if(idol) {
            this.EventText.setDialogOption(OPTIONS[8] + this.hpLossIdol + OPTIONS[1], new BloodyIdol());
        } else {
            this.EventText.setDialogOption(OPTIONS[0] + this.hpLoss1 + OPTIONS[1], new BloodyIdol());
        }

        if(vial){
            this.EventText.setDialogOption(OPTIONS[9] + this.hpLossVial + OPTIONS[1], new BloodRelic());

        } else{
            this.EventText.setDialogOption(OPTIONS[0] + this.hpLoss2 + OPTIONS[2], new BloodVial());
        }

        this.EventText.setDialogOption(OPTIONS[0] + this.hpLoss3 + OPTIONS[5], new AltarPotion());
        this.EventText.setDialogOption(OPTIONS[3], CardLibrary.getCopy("Decay"));
    }

    @Override
    public void update() {
        if (!this.combatTime) {
            this.hasFocus = true;
            if (MathUtils.randomBoolean(0.1F)) {
                AbstractDungeon.effectList.add(new EventBgParticle());
            }

            if (this.waitTimer > 0.0F) {
                this.waitTimer -= Gdx.graphics.getDeltaTime();
                if (this.waitTimer < 0.0F) {
                    this.EventText.show(this.title, this.body);
                    this.waitTimer = 0.0F;
                }
            }

            if (!GenericEventDialog.waitForInput) {
                this.buttonEffect(GenericEventDialog.getSelectedOption());
            }
        }
    }

    @Override
    public void updateDialog() {
        this.EventText.update();
        this.roomEventText.update();
    }

    @Override
    public void renderText(SpriteBatch sb) {
        this.roomEventText.render(sb);
        this.EventText.render(sb);
    }

    @Override
    public void dispose() {
        super.dispose();
        this.EventText.clear();
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
                        if(idol){
                            //metric logged in function
                            this.gainChalice();
                            AbstractDungeon.player.damage(new DamageInfo(null, this.hpLossIdol));
                            this.damageTaken += this.hpLossIdol;
                        }
                        else{
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2),
                                    (float)(Settings.HEIGHT / 2), new BloodyIdol());
                            AbstractDungeon.player.damage(new DamageInfo(null, this.hpLoss1));
                            this.damageTaken += this.hpLoss1;
                        }

                        this.optionsChosen += "Idol ";
                        CardCrawlGame.sound.play("BLOOD_SWISH");
                        //logMetricDamageAndMaxHPGain(ID, "Sacrifice", this.hpLoss, 5);
                        break;
                    case 1:
                        if(vial){
                            this.gainVial();
                            AbstractDungeon.player.damage(new DamageInfo(null, this.hpLossVial));
                            this.damageTaken += this.hpLossVial;
                        }
                        else{
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2),
                                    (float)(Settings.HEIGHT / 2), new BloodVial());
                            AbstractDungeon.player.damage(new DamageInfo(null, this.hpLoss2));
                            this.damageTaken += this.hpLoss2;
                        }

                        this.optionsChosen += "Vial ";
                        CardCrawlGame.sound.play("BLOOD_SWISH");
                        break;
                    case 2:
                        AbstractDungeon.effectList.add(new ObtainPotionEffect(new AltarPotion()));
                        AbstractDungeon.player.damage(new DamageInfo(null, this.hpLoss3));
                        this.damageTaken += this.hpLoss3;
                        this.optionsChosen += "Potion ";
                        CardCrawlGame.sound.play("BLOOD_SWISH");
                        //logMetricObtainCardAndRelic(ID, "Desecrate", curse, relic);
                        break;
                    case 3:
                        if(curse){
                            CardCrawlGame.sound.play("BLUNT_HEAVY");
                            CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.MED, true);
                            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new Decay(), (float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2)));
                            this.cardsGained.add(new Decay());
                            this.optionsChosen += "Defy";
                            logMetricObtainCardAndDamage(ID, this.optionsChosen, new Decay(), this.damageTaken);
                        }
                        break;
                    default:
                        break;
                }
                updateDialogs(buttonPressed);
                break;
            case 99:
                this.openMap();
                break;
            default:
                this.openMap();
                break;
        }
    }

    private void updateDialogs(int option){
        switch(option){
            case 0:
                curse = false;
                if(idol){
                    this.EventText.updateBodyText(DIALOG_2_ALT);
                }
                else{
                    this.EventText.updateBodyText(DIALOG_2);
                }
                this.EventText.updateDialogOption(option, OPTIONS[6], true);
                this.EventText.updateDialogOption(3, OPTIONS[7]);
                break;
            case 1:
                curse = false;
                if(vial){
                    this.EventText.updateBodyText(DIALOG_3_ALT);
                }
                else{
                    this.EventText.updateBodyText(DIALOG_3);
                }
                this.EventText.updateDialogOption(option, OPTIONS[6], true);
                this.EventText.updateDialogOption(3, OPTIONS[7]);
                break;
            case 2:
                curse = false;
                this.EventText.updateBodyText(DIALOG_4);
                this.EventText.updateDialogOption(option, OPTIONS[6], true);
                this.EventText.updateDialogOption(3, OPTIONS[7]);
                break;
            case 3:
                if(curse){
                    this.EventText.updateBodyText(DIALOG_5);
                } else{
                    this.EventText.updateBodyText(DIALOG_5_ALT);
                }
                this.EventText.updateDialogOption(0, OPTIONS[4]);
                this.EventText.clearRemainingOptions();
                this.screenNum = 99;
                break;
            default:
                this.EventText.updateBodyText("");
                this.EventText.updateDialogOption(0, OPTIONS[4]);
                this.EventText.clearRemainingOptions();
                this.screenNum = 99;
                break;
        }

    }

    private void gainChalice() {
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
            //logMetricRelicSwap(ID, "Gave Idol", new Circlet(), new GoldenIdol());
        } else {
            (AbstractDungeon.player.relics.get(relicAtIndex)).onUnequip();
            AbstractRelic bloodyIdol = RelicLibrary.getRelic(BloodyIdol.ID).makeCopy();
            bloodyIdol.instantObtain(AbstractDungeon.player, relicAtIndex, false);
            //logMetricRelicSwap(ID, "Gave Idol", new BloodyIdol(), new GoldenIdol());
        }
        this.optionsChosen += "Gold";
    }

    private void gainVial(){
        int relicAtIndex = 0;

        for(int i = 0; i < AbstractDungeon.player.relics.size(); ++i) {
            if ((AbstractDungeon.player.relics.get(i)).relicId.equals(BloodVial.ID)) {
                relicAtIndex = i;
                break;
            }
        }

        if (AbstractDungeon.player.hasRelic(BloodRelic.ID)) {
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2),
                    (float)(Settings.HEIGHT / 2), RelicLibrary.getRelic("Circlet").makeCopy());
        } else {
            (AbstractDungeon.player.relics.get(relicAtIndex)).onUnequip();
            AbstractRelic relic = RelicLibrary.getRelic(BloodRelic.ID).makeCopy();
            relic.instantObtain(AbstractDungeon.player, relicAtIndex, false);
        }
        this.optionsChosen += "Rotten";
    }

    static {
        DIALOG_2 = DESCRIPTIONS[1];
        DIALOG_3 = DESCRIPTIONS[2];
        DIALOG_4 = DESCRIPTIONS[3];
        DIALOG_5 = DESCRIPTIONS[4];
        DIALOG_2_ALT = DESCRIPTIONS[5];
        DIALOG_3_ALT = DESCRIPTIONS[6];
        DIALOG_5_ALT = DESCRIPTIONS[7];
    }
}
