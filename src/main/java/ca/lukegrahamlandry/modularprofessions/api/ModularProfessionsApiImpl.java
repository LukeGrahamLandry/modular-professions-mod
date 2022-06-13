package ca.lukegrahamlandry.modularprofessions.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

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
        return 0;
    }

    @Override
    public float getXp(Player player, ResourceLocation profession) {
        return 0;
    }

    @Override
    public void addXp(Player player, ResourceLocation profession, float amount) {

    }
}
