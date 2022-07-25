package ca.lukegrahamlandry.modularprofessions.api;

import ca.lukegrahamlandry.modularprofessions.ModMain;
import ca.lukegrahamlandry.modularprofessions.init.NetworkInit;
import ca.lukegrahamlandry.modularprofessions.network.clientbound.AddProfXpPacket;
import ca.lukegrahamlandry.modularprofessions.network.clientbound.ClearProfessionsPacket;
import ca.lukegrahamlandry.modularprofessions.network.clientbound.SyncProfessionJson;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = ModMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ProfessionDataLoader extends SimpleJsonResourceReloadListener {
    public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    public static List<Map.Entry<ResourceLocation, JsonElement>> toSync = new ArrayList<>();

    public ProfessionDataLoader() {
        super(GSON, "modularprofessions");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager p_10794_, ProfilerFiller p_10795_) {
        ModMain.LOGGER.debug("loading " + files.size() + " professions");
        for (ResourceLocation name : files.keySet()){
            JsonElement data = files.get(name);

            ModMain.API.parse(name, data.getAsJsonObject());
        }
        toSync.addAll(files.entrySet());
    }


    @SubscribeEvent
    public static void initCaps(AddReloadListenerEvent event){
        System.out.println("reload listener");

        toSync.clear();
        event.addListener(new ProfessionDataLoader());
    }

    @SubscribeEvent
    public static void syncProfessionsOnJoin(PlayerEvent.PlayerLoggedInEvent event){
        Player player = event.getPlayer();

        if (!player.level.isClientSide()){
            ModMain.LOGGER.debug("syncing " + toSync.size() + " files to client " + player.getScoreboardName());
            NetworkInit.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new ClearProfessionsPacket());

            for (Map.Entry<ResourceLocation, JsonElement> file : toSync){
                NetworkInit.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new SyncProfessionJson(file.getKey(), file.getValue()));
            }

            for (ResourceLocation profession : ModMain.API.getProfessions()) {
                NetworkInit.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new AddProfXpPacket(ModMain.API.getXp(player, profession), profession));
            }
        }
    }
}