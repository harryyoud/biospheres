package uk.co.harryyoud.biospheres;

import com.mojang.datafixers.Dynamic;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.provider.OverworldBiomeProviderSettings;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import uk.co.harryyoud.biospheres.config.BiosphereConfig;
import uk.co.harryyoud.biospheres.config.BiosphereGenSettingsSerializer;
import uk.co.harryyoud.biospheres.config.BiosphereGenSettingsSerializer.BiosphereGenSettings;
import uk.co.harryyoud.biospheres.gui.CreateBiospheresWorldScreen;

public class BiosphereWorldType extends WorldType {

	public BiosphereWorldType() {
		super("biospheres");
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator(World world) {
		if (world.getDimension().getType() != DimensionType.OVERWORLD) {
			return super.createChunkGenerator(world);
		}

		OverworldBiomeProviderSettings biomeProvSettings = new OverworldBiomeProviderSettings(world.getWorldInfo());
		BiosphereBiomeProvider biomeProv = new BiosphereBiomeProvider(world, biomeProvSettings);
		BiosphereGenSettings settings = BiosphereGenSettingsSerializer
				.get(new Dynamic<>(NBTDynamicOps.INSTANCE, world.getWorldInfo().getGeneratorOptions()));

		return new BiosphereChunkGenerator(world, biomeProv, settings);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void onCustomizeButton(Minecraft mc, CreateWorldScreen gui) {
		mc.displayGuiScreen(new CreateBiospheresWorldScreen(gui, gui.chunkProviderSettingsJson));
	}

	@Override
	public String getTranslationKey() {
		return "biospheres.generatorname";
	}

	@Override
	public boolean hasCustomOptions() {
		return true;
	}

	@Override
	public float getCloudHeight() {
		return BiosphereConfig.cloudHeight;
	}
}
