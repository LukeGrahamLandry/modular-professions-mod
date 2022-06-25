package ca.lukegrahamlandry.modularprofessions.event;

import ca.lukegrahamlandry.modularprofessions.ModMain;
import ca.lukegrahamlandry.modularprofessions.capability.ProfessionsXp;
import ca.lukegrahamlandry.modularprofessions.capability.ProfessionsXpCapProvider;
import ca.lukegrahamlandry.modularprofessions.init.NetworkInit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModCapEvents {
    @Mod.EventBusSubscriber(modid = ModMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeBus {
        @SubscribeEvent
        public static void attachCaps(AttachCapabilitiesEvent<Entity> event){
            if (event.getObject() instanceof Player){
                event.addCapability(new ResourceLocation(ModMain.MOD_ID, "professionxp"), new ProfessionsXpCapProvider());
            }
        }

        @SubscribeEvent
        public static void copyCaps(PlayerEvent.Clone event){
            if (!event.isWasDeath()) return;

            event.getOriginal().getCapability(ProfessionsXpCapProvider.CAP).ifPresent((oldXp) -> {
                event.getPlayer().getCapability(ProfessionsXpCapProvider.CAP).ifPresent((newXp) -> {
                    newXp.read(oldXp.write());
                });
            });
        }
    }

    @Mod.EventBusSubscriber(modid = ModMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBus {
        @SubscribeEvent
        public void registerCaps(RegisterCapabilitiesEvent event) {
            event.register(ProfessionsXp.class);
        }

        @SubscribeEvent
        public static void init(FMLCommonSetupEvent event){
            NetworkInit.registerPackets();
        }
    }
}
