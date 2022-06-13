package ca.lukegrahamlandry.modularprofessions.capability;

import ca.lukegrahamlandry.modularprofessions.ModMain;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ProfessionsXpImpl implements ProfessionsXp {
    private final Map<ResourceLocation, Float> XP = new HashMap<>();

    @Override
    public int getLevel(ResourceLocation profession) {
        return ModMain.API.getData(profession).getLevel(this.getXp(profession));
    }

    @Override
    public float getXp(ResourceLocation profession) {
        return XP.getOrDefault(profession, 0F);
    }

    @Override
    public void addXp(ResourceLocation profession, float amount) {
        float old = XP.getOrDefault(profession, 0F);
        XP.put(profession, old + amount);
    }

    @Override
    public CompoundTag write() {
        CompoundTag data = new CompoundTag();
        for (ResourceLocation profession : XP.keySet()){
            data.putFloat(profession.toString(), XP.get(profession));
        }
        return data;
    }

    @Override
    public void read(CompoundTag tag) {
        for (String profession : tag.getAllKeys()){
            XP.put(new ResourceLocation(profession), tag.getFloat(profession));
        }
    }
}
