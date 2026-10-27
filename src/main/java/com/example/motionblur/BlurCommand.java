package com.example.motionblur;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class BlurCommand extends CommandBase {
    @Override public String getCommandName() { return "blur"; }

    @Override public String getCommandUsage(ICommandSender s) {
        return "/blur <on|off|0-99>";
    }

    @Override public int getRequiredPermissionLevel() { return 0; }

    @Override public boolean canCommandSenderUseCommand(ICommandSender s) { return true; }

    @Override public void processCommand(ICommandSender s, String[] a) {
        if (a.length == 0) {
            say(s, "Uso: /blur <on|off|0-99>  (atual: "
                    + (MotionBlurMod.enabled ? MotionBlurMod.intensity : "off") + ")");
            return;
        }
        if (a[0].equalsIgnoreCase("on")) {
            MotionBlurMod.enabled = true;
            say(s, "Blur ligado (" + MotionBlurMod.intensity + ")");
        } else if (a[0].equalsIgnoreCase("off")) {
            MotionBlurMod.enabled = false;
            say(s, "Blur desligado");
        } else {
            try {
                int v = Integer.parseInt(a[0]);
                MotionBlurMod.intensity = Math.max(0, Math.min(99, v));
                MotionBlurMod.enabled = true;
                say(s, "Intensidade: " + MotionBlurMod.intensity);
            } catch (NumberFormatException e) {
                say(s, getCommandUsage(s));
            }
        }
    }

    private void say(ICommandSender s, String m) {
        s.addChatMessage(new ChatComponentText("§b[Blur] §f" + m));
    }
}
