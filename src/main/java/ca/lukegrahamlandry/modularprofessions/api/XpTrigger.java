package ca.lukegrahamlandry.modularprofessions.api;

import ca.lukegrahamlandry.modularprofessions.ModMain;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class XpTrigger {
    public static ResourceLocation CRAFTING = new ResourceLocation(ModMain.MOD_ID, "crafting");
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
            ItemCollection input = data.has("input") ? new ItemCollection(data.get("input"), onError) : new ItemCollection();
            ItemCollection output = data.has("output") ? new ItemCollection(data.get("output"), onError) : new ItemCollection();
            return new Crafting(input, output, data.get("amount").getAsFloat());
        } else {
            onError.accept(type + " is an invalid xp trigger type, SKIPPING");
        }

        return null;
    }



    public static class Crafting extends XpTrigger {
        ItemCollection input;
        ItemCollection output;
        public Crafting(ItemCollection input, ItemCollection output, float amount){
            super(amount);
            this.input = input;
            this.output = output;
        }
    }
}
