package betterAltar.relics;

import basemod.abstracts.CustomRelic;
import betterAltar.BetterAltar;
import betterAltar.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import static betterAltar.BetterAltar.makeRelicOutlinePath;
import static betterAltar.BetterAltar.makeRelicPath;

public class BloodRelic extends CustomRelic {

    public static final String ID = BetterAltar.makeID("BloodRelic");

    private static final Texture IMG = TextureLoader.getTexture(makeRelicPath("blood_vial.png"));
    private static final Texture OUTLINE = TextureLoader.getTexture(makeRelicOutlinePath("blood_vial.png"));

    private int healVal;


    public BloodRelic() {
        super(ID, IMG, OUTLINE, RelicTier.SPECIAL, AbstractRelic.LandingSound.CLINK);

        this.counter = -1;
        getHealVal();
    }

    @Override
    public String getUpdatedDescription() {
        getHealVal();
        return DESCRIPTIONS[0] + this.healVal + DESCRIPTIONS[1];
    }

    @Override
    public void onVictory() {
        updateDescription(AbstractDungeon.player.chosenClass);
    }

    private void getHealVal() {
        this.healVal = 2;
        if(CardCrawlGame.dungeon != null){
            this.healVal += AbstractDungeon.bossCount;
        }
    }

    @Override
    public void atBattleStart() {
        this.flash();
        this.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        this.addToTop(new HealAction(AbstractDungeon.player, AbstractDungeon.player, healVal, 0.0F));
    }

    @Override
    public void onLoseHp(int damageAmount) {
        CardCrawlGame.sound.play("BLOOD_SWISH");
    }

    @Override
    public void updateDescription(AbstractPlayer.PlayerClass c) {

        this.description = this.getUpdatedDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }
}
