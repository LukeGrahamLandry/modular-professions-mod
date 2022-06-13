package ca.lukegrahamlandry.modularprofessions.api;

import ca.lukegrahamlandry.modularprofessions.capability.ProfessionsXp;
import net.minecraft.resources.ResourceLocation;

public class ProfessionData implements LevelRule {
    private final ResourceLocation id;
    private final LevelRule leveling;

    public ProfessionData(ResourceLocation id, LevelRule leveling){
        this.id = id;
        this.leveling = leveling;
    }

    @Override
    public int getLevel(float xp) {
        return this.leveling.getLevel(xp);
    }

    public ResourceLocation getId() {
        return this.id;
    }
}
