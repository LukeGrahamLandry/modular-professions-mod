package ca.lukegrahamlandry.modularprofessions.event;


import ca.lukegrahamlandry.modularprofessions.ModMain;
import ca.lukegrahamlandry.modularprofessions.api.ProfessionData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LockedItemsEvents {
    @SubscribeEvent
    public static void onUseItem(PlayerInteractEvent.RightClickItem event){
        Player player = event.getPlayer();
        ItemStack item = event.getItemStack();

        if (!ModMain.API.canUseItem(player, item, ProfessionData.LockType.ITEM_USE)){
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
            player.displayClientMessage(new TextComponent("You cannot use ").append(item.getDisplayName()), true);
        }
    }

    @SubscribeEvent
    public static void onUseItemOnBlock(PlayerInteractEvent.RightClickBlock event){
        Player player = event.getPlayer();
        ItemStack item = event.getItemStack();
        BlockState state = event.getWorld().getBlockState(event.getHitVec().getBlockPos());
        ItemStack blockItem = new ItemStack(state.getBlock().asItem());

        if (!ModMain.API.canUseItem(player, item, ProfessionData.LockType.ITEM_USE)){
            event.setCanceled(true);
            player.displayClientMessage(new TextComponent("You cannot use ").append(item.getDisplayName()), true);
        }

        if (!ModMain.API.canUseItem(player, blockItem, ProfessionData.LockType.BLOCK_USE)) {
            event.setCanceled(true);
            player.displayClientMessage(new TextComponent("You cannot use ").append(blockItem.getDisplayName()), true);
        }
    }

    @SubscribeEvent
    public static void onUseItemOnEntity(PlayerInteractEvent.EntityInteract event){
        Player player = event.getPlayer();
        ItemStack item = event.getItemStack();

        if (!ModMain.API.canUseItem(player, item, ProfessionData.LockType.ITEM_USE)){
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
            player.displayClientMessage(new TextComponent("You cannot use ").append(item.getDisplayName()), true);
        }
    }

    @SubscribeEvent
    public static void onUseItem(PlayerInteractEvent.LeftClickBlock event){
        Player player = event.getPlayer();
        ItemStack item = event.getItemStack();

        if (!ModMain.API.canUseItem(player, item, ProfessionData.LockType.ITEM_USE)){
            event.setCanceled(true);
            player.displayClientMessage(new TextComponent("You cannot use ").append(item.getDisplayName()), true);
        }
    }
//
//    @SubscribeEvent
//    public static void onCraftItem(PlayerEvent.ItemCraftedEvent event){
//        Player player = event.getPlayer();
//        ItemStack item = event.getCrafting();
//
//        if (!ModMain.API.canUseItem(player, item, ProfessionData.LockType.CRAFT)){
//            item.setCount(0);
//            for (int i=0;i<event.getInventory().getContainerSize();i++){
//                ItemStack stack = event.getInventory().getItem(i);
//            }
//            player.displayClientMessage(new TextComponent("You cannot craft ").append(item.getDisplayName()), true);
//        }
//    }
}
