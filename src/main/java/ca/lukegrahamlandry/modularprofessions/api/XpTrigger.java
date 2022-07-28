package ca.lukegrahamlandry.modularprofessions.api;

import ca.lukegrahamlandry.modularprofessions.ModMain;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class XpTrigger {
    public static ResourceLocation CRAFTING = new ResourceLocation(ModMain.MOD_ID, "crafting");
    public static ResourceLocation BLOCK = new ResourceLocation(ModMain.MOD_ID, "block");
    public static ResourceLocation ENTITY_DAMAGE = new ResourceLocation(ModMain.MOD_ID, "deal_damage");
    public static ResourceLocation ENTITY_KILL = new ResourceLocation(ModMain.MOD_ID, "kill");

    float amount = 0;
    public XpTrigger(float amount){
        this.amount = amount;
    }

    public float getAmount(){
        return this.amount;
    }

    public static XpTrigger parse(JsonObject data, Consumer<String> onError) {
        if (!data.has("type")){
            onError.accept("xp trigger definition must have key 'type', SKIPPING");
            return null;
        }
        if (!data.has("amount")){
            onError.accept("xp trigger definition must have key 'amount', SKIPPING");
            return null;
        }
        ResourceLocation type = new ResourceLocation(data.get("type").getAsString());

        if (CRAFTING.equals(type)){
            ItemCollection<Item> input = data.has("input") ? new ItemCollection<>(data.get("input"), onError, ForgeRegistries.ITEMS, ItemTags::create) : new ItemCollection<>();
            ItemCollection<Item> output = data.has("output") ? new ItemCollection<>(data.get("output"), onError, ForgeRegistries.ITEMS, ItemTags::create) : new ItemCollection<>();
            return new Crafting(input, output, data.get("amount").getAsFloat());
        } else if (BLOCK.equals(type)){
            ItemCollection<Block> placed = data.has("placed") ? new ItemCollection<>(data.get("placed"), onError, ForgeRegistries.BLOCKS, BlockTags::create) : new ItemCollection<>();
            ItemCollection<Block> broken = data.has("broken") ? new ItemCollection<>(data.get("broken"), onError, ForgeRegistries.BLOCKS, BlockTags::create) : new ItemCollection<>();
            boolean allowSilkTouch = data.has("allowSilkTouch") && data.get("allowSilkTouch").getAsBoolean();
            JsonObject state = data.has("state") ? data.getAsJsonObject("state") : null;
            return new BlockInteract(placed, broken, allowSilkTouch, state, data.get("amount").getAsFloat());
        } else if (ENTITY_DAMAGE.equals(type)){
            ItemCollection<EntityType<?>> entity = data.has("entity") ? new ItemCollection<>(data.get("entity"), onError, ForgeRegistries.ENTITIES, (key) -> TagKey.create(Registry.ENTITY_TYPE_REGISTRY, key)) : ItemCollection.MATCH_ALL_ENTITIES;
            ItemCollection<Item> weapon = data.has("weapon") ? new ItemCollection<>(data.get("weapon"), onError, ForgeRegistries.ITEMS, ItemTags::create) : ItemCollection.MATCH_ALL_ITEMS;
            boolean multiplyXpByDamage = data.has("multiplyXpByDamage") && data.get("multiplyXpByDamage").getAsBoolean();
            return new EntityDamage(weapon, entity, multiplyXpByDamage, data.get("amount").getAsFloat());
         } else if (ENTITY_KILL.equals(type)){
            ItemCollection<EntityType<?>> entity = data.has("entity") ? new ItemCollection<>(data.get("entity"), onError, ForgeRegistries.ENTITIES, (key) -> TagKey.create(Registry.ENTITY_TYPE_REGISTRY, key)) : ItemCollection.MATCH_ALL_ENTITIES;
            ItemCollection<Item> weapon = data.has("weapon") ? new ItemCollection<>(data.get("weapon"), onError, ForgeRegistries.ITEMS, ItemTags::create) : ItemCollection.MATCH_ALL_ITEMS;
            return new EntityKill(weapon, entity, data.get("amount").getAsFloat());
        } else {
            onError.accept(type + " is an invalid xp trigger type, SKIPPING");
        }

        return null;
    }

    public static class Crafting extends XpTrigger {
        public final ItemCollection<Item> input;
        public final ItemCollection<Item> output;
        public Crafting(ItemCollection<Item> input, ItemCollection<Item> output, float amount){
            super(amount);
            this.input = input;
            this.output = output;
        }
    }

    public static class EntityDamage extends XpTrigger {
        public final ItemCollection<Item> weapon;
        public final ItemCollection<EntityType<?>> entity;
        public final boolean multiplyXpByDamage;
        public EntityDamage(ItemCollection<Item> weapon, ItemCollection<EntityType<?>> entity, boolean multiplyXpByDamage, float amount){
            super(amount);
            this.weapon = weapon;
            this.entity = entity;
            this.multiplyXpByDamage = multiplyXpByDamage;
        }
    }

    public static class EntityKill extends XpTrigger {
        public final ItemCollection<Item> weapon;
        public final ItemCollection<EntityType<?>> entity;
        public EntityKill(ItemCollection<Item> weapon, ItemCollection<EntityType<?>> entity, float amount){
            super(amount);
            this.weapon = weapon;
            this.entity = entity;
        }
    }

    public static class BlockInteract extends XpTrigger {
        public final ItemCollection<Block> placed;
        public final ItemCollection<Block> broken;
        public final boolean allowSilkTouch;
        public final JsonObject state;
        public BlockInteract(ItemCollection<Block> placed, ItemCollection<Block> broken, boolean allowSilkTouch, JsonObject state, float amount){
            super(amount);
            this.placed = placed;
            this.broken = broken;
            this.allowSilkTouch = allowSilkTouch;
            this.state = state;
        }

        public boolean statePropertiesMatch(BlockState state){
            if (this.state == null) return true;

            for (Map.Entry<String, JsonElement> requiredState : this.state.entrySet()){
                for (Property<?> prop : state.getProperties()){
                    if (requiredState.getKey().equals(prop.getName())){
                        if (prop.getValueClass() == Integer.class){
                            Integer value = requiredState.getValue().getAsInt();
                            if (value == state.getValue(prop)){
                                continue;
                            } else {
                                return false;
                            }
                        } else if (prop.getValueClass() == Boolean.class){
                            Boolean value = requiredState.getValue().getAsBoolean();
                            if (value == state.getValue(prop)){
                                continue;
                            } else {
                                return false;
                            }
                        } else {
                            String value = requiredState.getValue().getAsString();
                            if (value.equals(state.getValue(prop).toString())){
                                continue;
                            } else {
                                return false;
                            }
                        }
                    }

                    ModMain.LOGGER.error("checking XpTrigger.BlockInteract that matched " + state.getBlock().getRegistryName() + " but does not have property " + requiredState.getKey());
                    return false;
                }
            }

            return true;
        }
    }
}
