package ca.lukegrahamlandry.modularprofessions.mixin;

import ca.lukegrahamlandry.modularprofessions.ModMain;
import ca.lukegrahamlandry.modularprofessions.api.ProfessionData;
import ca.lukegrahamlandry.modularprofessions.init.NetworkInit;
import ca.lukegrahamlandry.modularprofessions.network.clientbound.SyncProfessionJson;
import com.google.gson.JsonElement;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.ReloadCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Mixin(ReloadCommand.class)
public class ReloadCommandMixin {
    @Inject(at=@At("HEAD"), method = "reloadPacks")
    private static void reloadPre(Collection<String> p_138236_, CommandSourceStack stack, CallbackInfo ci){
        ModMain.API.clearAll();
    }

    @Inject(at=@At("HEAD"), method = "reloadPacks")
    private static void reloadPost(Collection<String> p_138236_, CommandSourceStack stack, CallbackInfo ci){
        ModMain.API.resyncToClient();
    }
}
