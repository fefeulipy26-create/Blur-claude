package com.example.motionblur;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@Mod(modid = MotionBlurMod.MODID, name = "Motion Blur", version = "1.0",
        clientSideOnly = true, acceptedMinecraftVersions = "[1.8.9]")
public class MotionBlurMod {
    public static final String MODID = "motionblur";

    private static final ResourceLocation PHOSPHOR =
            new ResourceLocation("shaders/post/phosphor.json");

    public static boolean enabled = true;
    public static int intensity = 70;

    private final Minecraft mc = Minecraft.getMinecraft();
    private Field listShadersField;

    @EventHandler
    public void init(FMLInitializationEvent event) {
        listShadersField = ReflectionHelper.findField(
                ShaderGroup.class, "listShaders", "field_148031_d");
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new BlurCommand());
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END || mc.theWorld == null) return;

        ShaderGroup current = mc.entityRenderer.getShaderGroup();
        boolean ours = current != null && current.getShaderGroupName().contains("phosphor");

        if (!enabled) {
            if (ours) mc.entityRenderer.stopUseShader();
            return;
        }
        if (!OpenGlHelper.shadersSupported) return;

        if (current == null) {
            mc.entityRenderer.loadShader(PHOSPHOR);
            current = mc.entityRenderer.getShaderGroup();
            ours = current != null;
        }
        if (ours) applyIntensity(current);
    }

    @SuppressWarnings("unchecked")
    private void applyIntensity(ShaderGroup group) {
        try {
            float v = Math.max(0, Math.min(99, intensity)) / 100f;
            List<Shader> shaders = (List<Shader>) listShadersField.get(group);
            for (Shader s : shaders) {
                ShaderUniform u = s.getShaderManager().getShaderUniform("Phosphor");
                if (u != null) u.set(v, v, v);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
