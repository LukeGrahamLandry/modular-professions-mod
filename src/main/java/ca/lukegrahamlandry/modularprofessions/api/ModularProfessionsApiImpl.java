package ca.lukegrahamlandry.modularprofessions.api;

import ca.lukegrahamlandry.modularprofessions.capability.ProfessionsXp;
import ca.lukegrahamlandry.modularprofessions.capability.ProfessionsXpCapProvider;
import ca.lukegrahamlandry.modularprofessions.init.NetworkInit;
import ca.lukegrahamlandry.modularprofessions.network.clientbound.AddProfXpPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;

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
}
