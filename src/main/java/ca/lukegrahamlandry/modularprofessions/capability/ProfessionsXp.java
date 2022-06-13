package ca.lukegrahamlandry.modularprofessions.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public interface ProfessionsXp {
    int getLevel(ResourceLocation profession);
    float getXp(ResourceLocation profession);
    void addXp(ResourceLocation profession, float amount);

    CompoundTag write();
    void read(CompoundTag tag);
}
