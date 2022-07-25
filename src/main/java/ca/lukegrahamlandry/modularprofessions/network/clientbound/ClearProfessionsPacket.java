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

public class ClearProfessionsPacket {
    public ClearProfessionsPacket(FriendlyByteBuf buf) {
    }

    public void encode(FriendlyByteBuf buf){
    }

    public ClearProfessionsPacket(){
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(this::handle);
        ctx.get().setPacketHandled(true);
    }

    private void handle() {
        Player player = Minecraft.getInstance().player;
        if (player != null){
            ModMain.API.clearAll();
        }
    }
}