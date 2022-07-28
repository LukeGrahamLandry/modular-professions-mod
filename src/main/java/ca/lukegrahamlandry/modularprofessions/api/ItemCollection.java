package ca.lukegrahamlandry.modularprofessions.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ItemCollection<T extends IForgeRegistryEntry<T>> {
    List<String> mods = new ArrayList<>();
    List<ResourceLocation> objects = new ArrayList<>();
    List<TagKey<T>> tags = new ArrayList<>();

    public boolean isEmpty(){
        return mods.isEmpty() && objects.isEmpty() && tags.isEmpty();
    }

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

    public boolean contains(BlockState block){
        ResourceLocation key = block.getBlock().getRegistryName();
        if (mods.contains(key.getNamespace())) return true;
        if (objects.contains(key)) return true;
        for (TagKey tag : tags){
            if (block.is(tag)) return true;
        }
        return false;
    }

    public boolean contains(Entity entity){
        ResourceLocation key = entity.getType().getRegistryName();
        if (mods.contains(key.getNamespace())) return true;
        if (objects.contains(key)) return true;
        for (TagKey tag : tags){
            if (entity.getType().is(tag)) return true;
        }
        return false;
    }

    public void add(T item) {
        this.objects.add(item.getRegistryName());
    }

    public void add(TagKey tag) {
        this.tags.add(tag);
    }

    public void add(String modid) {
        this.mods.add(modid);
    }


    public ItemCollection(){

    }

    public ItemCollection(JsonElement data, Consumer<String> onError, IForgeRegistry<T> registry, Function<ResourceLocation, TagKey<T>> tagFactory){
        for (JsonElement item : data.getAsJsonArray()){
            String i = item.getAsString();
            if (i.contains(":")){
                T obj = registry.getValue(new ResourceLocation(i));
                if (obj == null){
                    onError.accept(  i + " is not registered");
                } else {
                    this.add(obj);
                }
            } else if (i.charAt(0) == '#'){
                TagKey<T> obj = tagFactory.apply(new ResourceLocation(i));
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



    public static final ItemCollection<Item> MATCH_ALL_ITEMS = new ItemCollection<>() {
        @Override
        public boolean contains(ItemStack item) {
            return true;
        }
    };

    public static final ItemCollection<Block> MATCH_ALL_BLOCKS = new ItemCollection<>() {
        @Override
        public boolean contains(BlockState item) {
            return true;
        }
    };

    public static final ItemCollection<EntityType<?>> MATCH_ALL_ENTITIES = new ItemCollection<>() {
        @Override
        public boolean contains(Entity item) {
            return true;
        }
    };
}
