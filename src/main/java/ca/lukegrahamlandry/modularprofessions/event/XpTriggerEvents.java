package ca.lukegrahamlandry.modularprofessions.event;


import ca.lukegrahamlandry.modularprofessions.ModMain;
import ca.lukegrahamlandry.modularprofessions.api.ProfessionData;
import ca.lukegrahamlandry.modularprofessions.api.XpTrigger;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = ModMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class XpTriggerEvents {
    // called by mixin
    public static void onCraft(Player player, ItemStack output, List<ItemStack> input) {
        ModMain.API.forEachProfessionTrigger((profession, trigger) -> {
            if (trigger instanceof XpTrigger.Crafting){
                boolean matches = false;
                if (((XpTrigger.Crafting) trigger).output.contains(output)){
                    matches = true;
                } else {
                    for (ItemStack in : input){
                        if (((XpTrigger.Crafting) trigger).input.contains(in)){
                            matches = true;
                            break;
                        }
                    }
                }

                if (matches) {
                    ModMain.API.awardXp(player, profession, trigger);
                }
            }
        });
    }

    @SubscribeEvent
    public static void onPlaceBlock(BlockEvent.EntityPlaceEvent event){
        if (event.getEntity() instanceof Player && !event.getEntity().level.isClientSide()){
            ModMain.API.forEachProfessionTrigger((profession, trigger) -> {
                if (trigger instanceof XpTrigger.BlockInteract){
                    if (((XpTrigger.BlockInteract) trigger).placed.contains(event.getPlacedBlock())){
                        if (((XpTrigger.BlockInteract) trigger).statePropertiesMatch(event.getPlacedBlock())){
                            ModMain.API.awardXp((Player) event.getEntity(), profession, trigger);
                        }
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void onBreakBlock(BlockEvent.BreakEvent event){
        if (!event.getPlayer().level.isClientSide()){
            boolean hasSilkTouch = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, event.getPlayer()) != 0;

            ModMain.API.forEachProfessionTrigger((profession, trigger) -> {
                if (trigger instanceof XpTrigger.BlockInteract){
                    if (((XpTrigger.BlockInteract) trigger).broken.contains(event.getState())){
                        if (hasSilkTouch && !((XpTrigger.BlockInteract) trigger).allowSilkTouch) return;
                        if (((XpTrigger.BlockInteract) trigger).statePropertiesMatch(event.getState())){
                            ModMain.API.awardXp(event.getPlayer(), profession, trigger);
                        }
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void onKillEntity(LivingDeathEvent event){
        LivingEntity target = event.getEntityLiving();
        Entity attacker = event.getSource().getEntity();
        if (!target.level.isClientSide() && attacker instanceof Player){
            ItemStack weapon = ((Player) attacker).getItemInHand(InteractionHand.MAIN_HAND);
            ModMain.API.forEachProfessionTrigger((profession, trigger) -> {
                if (trigger instanceof XpTrigger.EntityKill){
                    boolean weaponMatch = ((XpTrigger.EntityKill) trigger).weapon.contains(weapon);
                    boolean entityMatch = ((XpTrigger.EntityKill) trigger).entity.contains(target);

                    if (weaponMatch && entityMatch){
                        ModMain.API.awardXp((Player) attacker, profession, trigger);
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void onDamageEntity(LivingDamageEvent event){
        LivingEntity target = event.getEntityLiving();
        Entity attacker = event.getSource().getEntity();
        if (!target.level.isClientSide() && attacker instanceof Player){
            ItemStack weapon = ((Player) attacker).getItemInHand(InteractionHand.MAIN_HAND);
            ModMain.API.forEachProfessionTrigger((profession, trigger) -> {
                if (trigger instanceof XpTrigger.EntityDamage){
                    boolean weaponMatch = ((XpTrigger.EntityDamage) trigger).weapon.contains(weapon);
                    boolean entityMatch = ((XpTrigger.EntityDamage) trigger).entity.contains(target);

                    if (weaponMatch && entityMatch){
                        float amount = trigger.getAmount() * (((XpTrigger.EntityDamage) trigger).multiplyXpByDamage ? event.getAmount() : 1);
                        ModMain.API.addXp((Player) attacker, profession, amount);
                    }
                }
            });
        }
    }
}
