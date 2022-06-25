package ca.lukegrahamlandry.modularprofessions.network.clientbound;

import ca.lukegrahamlandry.modularprofessions.ModMain;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AddProfXpPacket {
    float amount;
    ResourceLocation profession;
    public AddProfXpPacket(FriendlyByteBuf buf) {
        this.amount = buf.readFloat();
        this.profession = buf.readResourceLocation();
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeFloat(amount);
        buf.writeResourceLocation(profession);
    }

    public AddProfXpPacket(float amount, ResourceLocation profession){
        this.amount = amount;
        this.profession = profession;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(this::handle);
        ctx.get().setPacketHandled(true);
    }

    private void handle() {
        Player player = Minecraft.getInstance().player;
        if (player != null){
            ModMain.API.addXp(player, profession, amount);
        }
    }
}