package ca.lukegrahamlandry.modularprofessions;

import ca.lukegrahamlandry.modularprofessions.api.LevelRule;
import ca.lukegrahamlandry.modularprofessions.api.ModularProfessionsApi;
import ca.lukegrahamlandry.modularprofessions.api.ModularProfessionsApiImpl;
import ca.lukegrahamlandry.modularprofessions.api.ProfessionData;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(ModMain.MOD_ID)
public class ModMain {
    public static final String MOD_ID = "modularprofessions";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final ModularProfessionsApi API = new ModularProfessionsApiImpl();

    public ModMain() {

    }
}
