package ca.lukegrahamlandry.modularprofessions.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class ItemCollection {
    List<String> mods = new ArrayList<>();
    List<ResourceLocation> objects = new ArrayList<>();
    List<TagKey<Item>> tags = new ArrayList<>();

    //TODO: tags wont work for USE_BLOCK because they check item tags on the blockitem

    public boolean contains(ItemStack item){
        ResourceLocation key = item.getItem().getRegistryName();
        if (mods.contains(key.getNamespace())) return true;
        if (objects.contains(key)) return true;
        for (TagKey<Item> tag : tags){
            if (item.is(tag)) return true;
        }
        return false;
    }

    public void add(Item item) {
        this.objects.add(item.getRegistryName());
    }

    public void add(TagKey<Item> tag) {
        this.tags.add(tag);
    }

    public void add(String modid) {
        this.mods.add(modid);
    }


    public ItemCollection(){

    }

    public ItemCollection(JsonElement data, Consumer<String> onError){
        for (JsonElement item : data.getAsJsonArray()){
            String i = item.getAsString();
            if (i.contains(":")){
                Item obj = ForgeRegistries.ITEMS.getValue(new ResourceLocation(i));
                if (obj == null){
                    onError.accept(  i + " is not a registered item");
                } else {
                    this.add(obj);
                }
            } else if (i.charAt(0) == '#'){
                TagKey<Item> obj = ItemTags.create(new ResourceLocation(i));
                this.add(obj);
            } else {
                if (ModList.get().isLoaded(i)) {
                    this.add(i);
                } else {
                    onError.accept(i + " mod is not loaded");
                }
            }
        }
    }
}
