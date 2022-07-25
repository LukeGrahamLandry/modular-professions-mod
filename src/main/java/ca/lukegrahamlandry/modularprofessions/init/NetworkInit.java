package ca.lukegrahamlandry.modularprofessions.init;

import ca.lukegrahamlandry.modularprofessions.ModMain;
import ca.lukegrahamlandry.modularprofessions.network.clientbound.AddProfXpPacket;
import ca.lukegrahamlandry.modularprofessions.network.clientbound.ClearProfessionsPacket;
import ca.lukegrahamlandry.modularprofessions.network.clientbound.SyncProfessionJson;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkInit {
    public static SimpleChannel INSTANCE;
    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void registerPackets() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(ModMain.MOD_ID, "packets"), () -> "1.0", s -> true, s -> true);

        INSTANCE.registerMessage(nextID(), AddProfXpPacket.class, AddProfXpPacket::encode, AddProfXpPacket::new, AddProfXpPacket::handle);
        INSTANCE.registerMessage(nextID(), SyncProfessionJson.class, SyncProfessionJson::encode, SyncProfessionJson::new, SyncProfessionJson::handle);
        INSTANCE.registerMessage(nextID(), ClearProfessionsPacket.class, ClearProfessionsPacket::encode, ClearProfessionsPacket::new, ClearProfessionsPacket::handle);
    }
}