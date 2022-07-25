package ca.lukegrahamlandry.modularprofessions.mixin;

import ca.lukegrahamlandry.modularprofessions.ModMain;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ResultSlot.class)
public class ResultSlotMixin {
    @Shadow @Final private CraftingContainer craftSlots;

    @Inject(at=@At("HEAD"), method = "onTake")
    private void awardXpForCrafting(Player player, ItemStack stack, CallbackInfo ci){
        System.out.println("crafting onTake mixin");
        if (!player.level.isClientSide) {
            ModMain.API.handleCraftingTriggers(player, stack, toStackList(this.craftSlots));
        }
    }

    private List<ItemStack> toStackList(CraftingContainer craftSlots) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i=0;i<craftSlots.getContainerSize();i++){
            ItemStack stack = craftSlots.getItem(i);
            if (!stack.isEmpty()) stacks.add(stack.copy());
        }
        return stacks;
    }
}
