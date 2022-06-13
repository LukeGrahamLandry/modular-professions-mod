package ca.lukegrahamlandry.modularprofessions.capability;

import ca.lukegrahamlandry.modularprofessions.ModMain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ModCapEvents {
    @Mod.EventBusSubscriber(modid = ModMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeBus {
        @SubscribeEvent
        public static void attahCaps(AttachCapabilitiesEvent<Entity> event){
            if (event.getObject() instanceof Player){
                event.addCapability(new ResourceLocation(ModMain.MOD_ID, "professionxp"), new ProfessionsXpCapProvider());
            }


        }

        @SubscribeEvent
        public static void copyCaps(PlayerEvent.Clone event){
            if (!event.isWasDeath()) return;


        }
    }

    @Mod.EventBusSubscriber(modid = ModMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBus {
        @SubscribeEvent
        public void registerCaps(RegisterCapabilitiesEvent event) {
            event.register(ProfessionsXp.class);
        }
    }
}
