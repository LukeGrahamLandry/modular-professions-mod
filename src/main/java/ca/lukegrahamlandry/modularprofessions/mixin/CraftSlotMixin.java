package ca.lukegrahamlandry.modularprofessions.mixin;

import ca.lukegrahamlandry.modularprofessions.ModMain;
import ca.lukegrahamlandry.modularprofessions.api.ProfessionData;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(CraftingMenu.class)
public class CraftSlotMixin {
    @Inject(at=@At("HEAD"), method = "slotChangedCraftingGrid", cancellable = true)
    private static void mayPickupPreventLockedItems(AbstractContainerMenu p_150547_, Level p_150548_, Player p_150549_, CraftingContainer p_150550_, ResultContainer p_150551_, CallbackInfo ci){
        System.out.println("slotChangedCraftingGrid mixin");
        if (!p_150548_.isClientSide) {
            ServerPlayer serverplayer = (ServerPlayer)p_150549_;
            ItemStack itemstack = ItemStack.EMPTY;
            Optional<CraftingRecipe> optional = p_150548_.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, p_150550_, p_150548_);
            if (optional.isPresent()) {
                CraftingRecipe craftingrecipe = optional.get();
                if (p_150551_.setRecipeUsed(p_150548_, serverplayer, craftingrecipe)) {
                    itemstack = craftingrecipe.assemble(p_150550_);
                }
            }

            if (!itemstack.isEmpty() && !ModMain.API.canUseItem(p_150549_, itemstack, ProfessionData.LockType.CRAFT)){
                ci.cancel();
                p_150551_.setItem(0, ItemStack.EMPTY);
            }
        }
    }
}
