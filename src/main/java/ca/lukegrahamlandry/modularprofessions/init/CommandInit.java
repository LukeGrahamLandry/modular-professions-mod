package ca.lukegrahamlandry.modularprofessions.init;

import ca.lukegrahamlandry.modularprofessions.ModMain;
import ca.lukegrahamlandry.modularprofessions.command.ProfessionXpCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommandInit {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event){
        ProfessionXpCommand.register(event.getDispatcher());
    }
}