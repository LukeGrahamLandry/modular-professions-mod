package ca.lukegrahamlandry.modularprofessions.api;

import ca.lukegrahamlandry.modularprofessions.capability.ProfessionsXp;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;

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


    ArrayList<HashMap<LockType, ItemCollection>> lockedItems = new ArrayList<>();

    public boolean unlockedAtLevel(ItemStack stack, int level, LockType type){
        for (int i=0;i<=level;i++){
            if (lockedItems.size() <= i) break;

            ItemCollection collection = lockedItems.get(i).get(type);
            if (collection.contains(stack)) return true;
        }
        return false;
    }

    public boolean isRestricted(ItemStack stack, LockType type) {
        return unlockedAtLevel(stack, lockedItems.size()-1, type);
    }

    private void createLevels(int level){
        while (lockedItems.size() <= level){
            HashMap<LockType, ItemCollection> map = new HashMap<>();
            map.put(LockType.CRAFT, new ItemCollection());
            map.put(LockType.ITEM_USE, new ItemCollection());
            map.put(LockType.BLOCK_USE, new ItemCollection());
            lockedItems.add(map);
        }
    }


    public void addLockedItem(Item item, int unlockLevel, LockType type) {
        createLevels(unlockLevel);
        ItemCollection collection = lockedItems.get(unlockLevel).get(type);
        collection.add(item);
    }

    public void addLockedTag(TagKey<Item> tag, int unlockLevel, LockType type) {
        createLevels(unlockLevel);
        ItemCollection collection = lockedItems.get(unlockLevel).get(type);
        collection.add(tag);
    }

    public void addLockedMod(String modid, int unlockLevel, LockType type) {
        createLevels(unlockLevel);
        ItemCollection collection = lockedItems.get(unlockLevel).get(type);
        collection.add(modid);
    }

    public enum LockType {
        CRAFT,
        ITEM_USE,
        BLOCK_USE
    }
}
