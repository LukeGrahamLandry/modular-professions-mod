package ca.lukegrahamlandry.modularprofessions.api;

import ca.lukegrahamlandry.modularprofessions.ModMain;
import ca.lukegrahamlandry.modularprofessions.capability.ProfessionsXp;
import ca.lukegrahamlandry.modularprofessions.capability.ProfessionsXpCapProvider;
import ca.lukegrahamlandry.modularprofessions.init.NetworkInit;
import ca.lukegrahamlandry.modularprofessions.network.clientbound.AddProfXpPacket;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class ModularProfessionsApiImpl implements ModularProfessionsApi {
    private final Map<ResourceLocation, ProfessionData> PROFESSION_REGISTRY = new HashMap<>();

    @Override
    public void registerProfession(ProfessionData profession) {
        PROFESSION_REGISTRY.put(profession.getId(), profession);
    }

    @Override
    public ProfessionData getData(ResourceLocation profession) {
        return PROFESSION_REGISTRY.get(profession);
    }

    @Override
    public List<ResourceLocation> getProfessions() {
        return new ArrayList<>(PROFESSION_REGISTRY.keySet());
    }

    @Override
    public int getLevel(Player player, ResourceLocation profession) {
        if (!PROFESSION_REGISTRY.containsKey(profession)) return 0;
        return getData(profession).getLevel(getXp(player, profession));
    }

    @Override
    public float getXp(Player player, ResourceLocation profession) {
        if (!PROFESSION_REGISTRY.containsKey(profession)) return 0;

        LazyOptional<ProfessionsXp> xp = player.getCapability(ProfessionsXpCapProvider.CAP);
        if (xp.isPresent()){
            return xp.resolve().get().getXp(profession);
        } else {
            return 0;
        }
    }

    @Override
    public void addXp(Player player, ResourceLocation profession, float amount) {
        player.getCapability(ProfessionsXpCapProvider.CAP).ifPresent((xp) -> {
            xp.addXp(profession, amount);
        });
        if (!player.level.isClientSide()){
            NetworkInit.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new AddProfXpPacket(amount, profession));
        }
    }

    @Override
    public boolean canUseItem(Player player, ItemStack stack, ProfessionData.LockType type) {
        LazyOptional<ProfessionsXp> xpHolder = player.getCapability(ProfessionsXpCapProvider.CAP);
        if (!xpHolder.isPresent()) return false;
        ProfessionsXp xp = xpHolder.resolve().get();

        boolean isEverLocked = false;
        for (ResourceLocation key : getProfessions()){
            ProfessionData profession = getData(key);
            if (profession.unlockedAtLevel(stack, xp.getLevel(key), type)) return true;
            if (profession.isRestricted(stack, type)) isEverLocked = true;
        }
        return !isEverLocked;
    }

    private static void debug(String msg){
        ModMain.LOGGER.debug("[loading error]" + msg);
    }

    @Override  // TODO sync the json string to client so i can call this there
    public void parse(ResourceLocation name, JsonObject data) {
        ProfessionData profession;
        if (PROFESSION_REGISTRY.containsKey(name)){
            profession = getData(name);
        } else {
            if (!data.has("leveling") || LevelRule.parse(data.getAsJsonObject("leveling")) == null){
                debug(name + " has an invalid leveling rule. skipping!");
                return;
            }
            profession = new ProfessionData(name, LevelRule.parse(data.getAsJsonObject("leveling")));
            registerProfession(profession);
        }

        if (data.has("unlocked")) parseUnlocked(profession, data.get("unlocked"));
    }

    private void parseUnlocked(ProfessionData profession, JsonElement data){
        int level = 1;
        for (JsonElement levelUnlockData : data.getAsJsonArray()){
            for (ProfessionData.LockType type : ProfessionData.LockType.values()){
                String key = type.name().toLowerCase(Locale.ROOT);
                JsonObject itemObj = levelUnlockData.getAsJsonObject();
                if (itemObj.has(key)) {
                    JsonArray items = itemObj.getAsJsonArray(key);
                    for (JsonElement item : items){
                        String i = item.getAsString();
                        if (i.contains(":")){
                            Item obj = ForgeRegistries.ITEMS.getValue(new ResourceLocation(i));
                            if (obj == null){
                                debug(key + " unlocked at " + profession.getId() + " level " + level + ": " + i + " is an invalid item");
                            } else {
                                profession.addLockedItem(obj, level, type);
                            }
                        } else if (i.charAt(0) == '#'){
                            TagKey<Item> obj = ItemTags.create(new ResourceLocation(i));
                            profession.addLockedTag(obj, level, type);
                        } else {
                            if (ModList.get().isLoaded(i)) {
                                profession.addLockedMod(i, level, type);
                            } else {
                                debug(key + " unlocked at " + profession.getId() + " level " + level + ": " + i + " mod is not loaded");
                            }
                        }
                    }
                }
            }
            level++;
        }
    }
}
