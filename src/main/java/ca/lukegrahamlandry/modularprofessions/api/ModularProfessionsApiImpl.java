package ca.lukegrahamlandry.modularprofessions.api;

import ca.lukegrahamlandry.modularprofessions.capability.ProfessionsXp;
import ca.lukegrahamlandry.modularprofessions.capability.ProfessionsXpCapProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;

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
        player.getCapability(ProfessionsXpCapProvider.CAP).ifPresent((xp) -> xp.addXp(profession, amount));
    }
}
