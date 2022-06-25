package ca.lukegrahamlandry.modularprofessions;

import ca.lukegrahamlandry.modularprofessions.api.LevelRule;
import ca.lukegrahamlandry.modularprofessions.api.ModularProfessionsApi;
import ca.lukegrahamlandry.modularprofessions.api.ModularProfessionsApiImpl;
import ca.lukegrahamlandry.modularprofessions.api.ProfessionData;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.stream.Collectors;

@Mod(ModMain.MOD_ID)
public class ModMain {
    public static final String MOD_ID = "modularprofessions";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final ModularProfessionsApi API = new ModularProfessionsApiImpl();

    public ModMain() {
        ProfessionData blacksmith = new ProfessionData(new ResourceLocation(ModMain.MOD_ID, "blacksmith"), new LevelRule.Polynomial(2));

        // prevents right or left clicking the item
        blacksmith.addLockedItem(Items.BOW, 1, ProfessionData.LockType.ITEM_USE);
        blacksmith.addLockedItem(Items.IRON_PICKAXE, 2, ProfessionData.LockType.ITEM_USE);

        // prevents right clicking the block when placed in the world
        blacksmith.addLockedItem(Blocks.CRAFTING_TABLE.asItem(), 2, ProfessionData.LockType.BLOCK_USE);

        // prevents crafting
        blacksmith.addLockedItem(Items.STICK, 2, ProfessionData.LockType.CRAFT);

        API.registerProfession(new ProfessionData(new ResourceLocation(ModMain.MOD_ID, "archer"), new LevelRule.Linear(10)));
        API.registerProfession(blacksmith);
    }
}
