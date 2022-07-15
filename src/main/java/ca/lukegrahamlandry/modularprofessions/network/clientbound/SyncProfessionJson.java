package ca.lukegrahamlandry.modularprofessions.network.clientbound;

import ca.lukegrahamlandry.modularprofessions.ModMain;
import ca.lukegrahamlandry.modularprofessions.api.ProfessionDataLoader;
import com.google.gson.JsonElement;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncProfessionJson {
    private static final int MAX = 32767 * 2;
    JsonElement data;
    ResourceLocation profession;

    public SyncProfessionJson(FriendlyByteBuf buf) {
        this.profession = buf.readResourceLocation();
        this.data = GsonHelper.fromJson(ProfessionDataLoader.GSON, buf.readUtf(MAX), JsonElement.class);
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeResourceLocation(profession);
        buf.writeUtf(this.data.toString());
    }

    public SyncProfessionJson(ResourceLocation profession, JsonElement data){
        this.profession = profession;
        this.data = data;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(this::handle);
        ctx.get().setPacketHandled(true);
    }

    private void handle() {
        Player player = Minecraft.getInstance().player;
        if (player != null){
            ModMain.API.parse(profession, data.getAsJsonObject());
        }
    }
}