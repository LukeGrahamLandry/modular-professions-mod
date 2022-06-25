package ca.lukegrahamlandry.modularprofessions.api;

import ca.lukegrahamlandry.modularprofessions.ModMain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface ModularProfessionsApi {
    static ModularProfessionsApi get(){
        return ModMain.API;
    }


    void registerProfession(ProfessionData data);
    ProfessionData getData(ResourceLocation profession);
    List<ResourceLocation> getProfessions();

    int getLevel(Player player, ResourceLocation profession);
    float getXp(Player player, ResourceLocation profession);
    void addXp(Player player, ResourceLocation profession, float amount);

    boolean canUseItem(Player player, ItemStack stack, ProfessionData.LockType type);

}
