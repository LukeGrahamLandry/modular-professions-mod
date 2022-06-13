package ca.lukegrahamlandry.modularprofessions.command;

import ca.lukegrahamlandry.modularprofessions.ModMain;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class ProfessionXpCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(register());
    }

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("professionxp").then(
                Commands.literal("add")
                        .requires(cs->cs.hasPermission(0)) //permission
                        .then(Commands.argument("profession", new ProfessionArgumentType())
                                .then(Commands.argument("amount", FloatArgumentType.floatArg()).executes(ProfessionXpCommand::handleAddXp))
                        )
        ).then(Commands.literal("show").executes(ProfessionXpCommand::handleshowXP));

    }

    public static int handleAddXp(CommandContext<CommandSourceStack> source) throws CommandSyntaxException {
        Player player = source.getSource().getPlayerOrException();
        ResourceLocation profession = ProfessionArgumentType.get(source, "profession");
        float amount = FloatArgumentType.getFloat(source, "amount");

        ModMain.API.addXp(player, profession, amount);

        source.getSource().sendSuccess(new TextComponent("Added " + amount + " xp to " + profession).withStyle(ChatFormatting.LIGHT_PURPLE), true);

        return Command.SINGLE_SUCCESS;
    }

    public static int handleshowXP(CommandContext<CommandSourceStack> source) throws CommandSyntaxException {
        Player player = source.getSource().getPlayerOrException();
        for (ResourceLocation profession : ModMain.API.getProfessions()){
            int level =  ModMain.API.getLevel(player, profession);
            float xp =  ModMain.API.getXp(player, profession);
            source.getSource().sendSuccess(new TextComponent(profession + " level " + level + " (" + xp +" xp) ").withStyle(ChatFormatting.AQUA), true);
        }

        return Command.SINGLE_SUCCESS;
    }
}
