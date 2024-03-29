package betterAltar.patches;


import betterAltar.BetterAltar;
import betterAltar.events.BetterAltarEvent;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.rooms.VictoryRoom;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

public class customMetrics implements Runnable {

    private HashMap<Object, Object> params = new HashMap<>();
    private Gson gson = new Gson();
    private long lastPlaytimeEnd;
    private boolean foundEvent = false;
    public boolean death;
    public boolean trueVictory;
    public MonsterGroup monsters = null;
    public static final SimpleDateFormat timestampFormatter = new SimpleDateFormat("yyyyMMddHHmmss");

    public static final String URL = "https://metricsanalysis.azurewebsites.net/metrics";

    public void setValues(boolean death, boolean trueVictor, MonsterGroup monsters) {
        this.death = death;
        this.trueVictory = trueVictor;
        this.monsters = monsters;
    }

    private void addData(Object key, Object value)
    {
        this.params.put(key, value);
    }

    private void sendPost()
    {
        LinkedHashMap<String, Serializable> event = new LinkedHashMap<>();

        // REMEMBER TO UPDATE
        event.put("title", "BetterAltar");
        // REMEMBER TO UPDATE

        event.put("event", this.params);
        event.put("name", CardCrawlGame.playerName);
        event.put("alias", CardCrawlGame.alias);

        event.put("time", Long.valueOf(System.currentTimeMillis() / 1000L));
        String data = this.gson.toJson(event);
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();

        Net.HttpRequest httpRequest = requestBuilder.newRequest().method("POST").url(URL).header("Content-Type", "application/json").header("Accept", "application/json").header("User-Agent", "curl/7.43.0").build();
        httpRequest.setContent(data);
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener()
        {
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                BetterAltar.logger.info("Metrics: http request response: " + httpResponse.getResultAsString());
            }

            public void failed(Throwable t) {
                BetterAltar.logger.info("Metrics: http request failed: " + t.toString());
            }

            public void cancelled() {
                BetterAltar.logger.info("Metrics: http request cancelled.");
            }
        });
    }

    private void gatherAllData()
    {
        addData("play_id", UUID.randomUUID().toString());
        addData("build_version", CardCrawlGame.TRUE_VERSION_NUM);
        addData("seed_played", Settings.seed.toString());
        //addData("chose_seed", Boolean.valueOf(Settings.seedSet));
        //addData("seed_source_timestamp", Long.valueOf(Settings.seedSourceTimestamp));
        //addData("is_daily", Boolean.valueOf(Settings.isDailyRun));
        //addData("special_seed", Settings.specialSeed);
        //addData("is_trial", Boolean.valueOf(Settings.isTrial));
        //addData("is_endless", Boolean.valueOf(Settings.isEndless));
        /*
        if (death)
        {
            AbstractPlayer player = AbstractDungeon.player;
            CardCrawlGame.metricData.current_hp_per_floor.add(Integer.valueOf(player.currentHealth));
            CardCrawlGame.metricData.max_hp_per_floor.add(Integer.valueOf(player.maxHealth));
            CardCrawlGame.metricData.gold_per_floor.add(Integer.valueOf(player.gold));
        }
        */
        //addData("is_ascension_mode", Boolean.valueOf(AbstractDungeon.isAscensionMode));
        addData("ascension_level", Integer.valueOf(AbstractDungeon.ascensionLevel));

        //addData("neow_bonus", CardCrawlGame.metricData.neowBonus);
        //addData("neow_cost", CardCrawlGame.metricData.neowCost);

        //addData("is_beta", Boolean.valueOf(Settings.isBeta));
        //addData("is_prod", Boolean.valueOf(Settings.isDemo));
        addData("victory", !death);
        addData("floor_reached", Integer.valueOf(AbstractDungeon.floorNum));
        if (this.trueVictory) {
            this.addData("score", VictoryScreen.calcScore(!death));
        } else {
            this.addData("score", DeathScreen.calcScore(!death));
        }
        this.lastPlaytimeEnd = (System.currentTimeMillis() / 1000L);
        addData("timestamp", Long.valueOf(this.lastPlaytimeEnd));
        //addData("local_time", timestampFormatter.format(Calendar.getInstance().getTime()));
        addData("playtime", Float.valueOf(CardCrawlGame.playtime));
        addData("player_experience", Long.valueOf(Settings.totalPlayTime));
        addData("master_deck", getDeck());
        //addData("relics", AbstractDungeon.player.getRelicNames());
        //addData("gold", Integer.valueOf(AbstractDungeon.player.gold));
        //addData("campfire_rested", Integer.valueOf(CardCrawlGame.metricData.campfire_rested));
        //addData("campfire_upgraded", Integer.valueOf(CardCrawlGame.metricData.campfire_upgraded));
        //addData("purchased_purges", Integer.valueOf(CardCrawlGame.metricData.purchased_purges));
        //addData("potions_floor_spawned", CardCrawlGame.metricData.potions_floor_spawned);
        addData("potions_floor_usage", CardCrawlGame.metricData.potions_floor_usage);
        //addData("current_hp_per_floor", CardCrawlGame.metricData.current_hp_per_floor);
        //addData("max_hp_per_floor", CardCrawlGame.metricData.max_hp_per_floor);
        //addData("gold_per_floor", CardCrawlGame.metricData.gold_per_floor);
        //addData("path_per_floor", CardCrawlGame.metricData.path_per_floor);
        //addData("path_taken", CardCrawlGame.metricData.path_taken);
        //addData("items_purchased", CardCrawlGame.metricData.items_purchased);
        //addData("item_purchase_floors", CardCrawlGame.metricData.item_purchase_floors);
        //addData("items_purged", CardCrawlGame.metricData.items_purged);
        //addData("items_purged_floors", CardCrawlGame.metricData.items_purged_floors);
        //addData("card_choices", CardCrawlGame.metricData.card_choices);
        //addData("boss_relics", CardCrawlGame.metricData.boss_relics);
        //addData("damage_taken", CardCrawlGame.metricData.damage_taken);
        //addData("potions_obtained", CardCrawlGame.metricData.potions_obtained);
        //addData("relics_obtained", CardCrawlGame.metricData.relics_obtained);
        //addData("campfire_choices", CardCrawlGame.metricData.campfire_choices);

        addData("character_chosen", AbstractDungeon.player.chosenClass.name());

        if (death && monsters != null) {
            this.addData("killed_by", AbstractDungeon.lastCombatMetricKey);
        } else {
            this.addData("killed_by", null);
        }

        //addData("event_choices", CardCrawlGame.metricData.event_choices);
        addData("heal_limit", BetterAltar.healthLimit);
        //Document document = builder.parse(new File("pom.xml"));
        //addData("version", pom.xml.version);
        addData("mods", getModList());
    }

    private String getModList(){
        StringBuilder retVal = new StringBuilder();
        String mod;
        for(int i = 0; i < Loader.MODINFOS.length; ++i) {
            if(i != 0){
                retVal.append("-|-");
            }
            if(Loader.MODINFOS[i].Name != null){
                mod = StringUtils.substring(Loader.MODINFOS[i].Name, 0, 30);
                mod = mod.replace("'", "");
                mod = mod.replace("`", "");
            }
            else{
                mod = " ";
            }
            retVal.append(mod);
            if(retVal.length() >= 1950){
                retVal.append("---MAX SIZE REACHED!!!");
                break;
            }
        }
        return retVal.toString();
    }

    private HashMap getDeck(){
        HashMap<String, Integer> map = new HashMap<>();
        for(AbstractCard card: AbstractDungeon.player.masterDeck.group){
            if(!map.containsKey(card.name)){
                map.put(card.name, 1);
            }
            else{
                int x = map.get(card.name);
                x++;
                map.remove(card.name);
                map.put(card.name, x);
            }
        }
        return map;
    }

    public void run()
    {
        for(HashMap map : CardCrawlGame.metricData.event_choices){
            if(map.get("event_name").equals(BetterAltarEvent.ID)){
                foundEvent = true;
                addData("event_choice", map);
                break;
            }
        }

        if (foundEvent) {
            if (Settings.UPLOAD_DATA && Settings.isStandardRun() && !Settings.seedSet && !Settings.isEndless) {
                gatherAllData();
                sendPost();
            }
        }
    }
}