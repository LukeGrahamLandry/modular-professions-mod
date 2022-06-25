package ca.lukegrahamlandry.modularprofessions.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class ItemCollection {
    List<String> mods = new ArrayList<>();
    List<ResourceLocation> objects = new ArrayList<>();
    List<TagKey> tags = new ArrayList<>();

    //TODO: tags wont work for USE_BLOCK because they check item tags on the blockitem

    public boolean contains(ItemStack item){
        ResourceLocation key = item.getItem().getRegistryName();
        if (mods.contains(key.getNamespace())) return true;
        if (objects.contains(key)) return true;
        for (TagKey tag : tags){
            if (item.is(tag)) return true;
        }
        return false;
    }

    public void add(Item item) {
        this.objects.add(item.getRegistryName());
    }

    public void add(TagKey tag) {
        this.tags.add(tag);
    }
}
