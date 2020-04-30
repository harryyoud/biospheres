package uk.co.harryyoud.biospheres;

import java.nio.file.Paths;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.server.dedicated.PropertyManager;
import net.minecraft.server.dedicated.ServerProperties;
import net.minecraft.world.WorldType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper.UnableToAccessFieldException;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper.UnableToFindFieldException;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import uk.co.harryyoud.biospheres.config.BiosphereConfig;
import uk.co.harryyoud.biospheres.config.BiosphereGenSettingsSerializer;

@Mod(Biospheres.MODID)
public class Biospheres {

	public static final String MODID = "biospheres";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static final BiosphereWorldType worldType = new BiosphereWorldType();

	public Biospheres() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::dedicatedServerSetup);
		BiosphereConfig.setup();
	}

	public void dedicatedServerSetup(FMLDedicatedServerSetupEvent event) {
		ServerProperties props = event.getServerSupplier().get().getServerProperties();

		if (!BiosphereConfig.shouldInjectWorldType) {
			System.out.println("World type injection disabled, not injecting biospheres world type");
			return;
		}

		if (props.worldType != WorldType.DEFAULT) {
			System.out.println("World type injection enabled, but level-type is not default, aborting");
			return;
		}

		System.out.println(String
				.format("Biospheres injection is enabled, injecting biospheres level-type and generator-settings"));
		System.out.println(String.format("Original level-type=\"%s\"", props.worldType.getName()));
		System.out.println(String.format("Original generator-settings=\"%s\"", props.generatorSettings));

		String newGenSettings = Dynamic
				.convert(NBTDynamicOps.INSTANCE, JsonOps.INSTANCE,
						(CompoundNBT) (new BiosphereGenSettingsSerializer()).toNBT(NBTDynamicOps.INSTANCE).getValue())
				.toString();

		// First, we'll override the server.properties in memory
		try {
			ObfuscationReflectionHelper.setPrivateValue(ServerProperties.class, props, worldType, "field_219023_q");

			if (props.generatorSettings.isEmpty()) {
				ObfuscationReflectionHelper.setPrivateValue(ServerProperties.class, props, newGenSettings,
						"field_219024_r");
			}
		} catch (UnableToFindFieldException | UnableToAccessFieldException e) {
			throw new Error("Reflection failed when trying to edit the level-type and generator-settings properties",
					e);
		}

		// Then, we replace them and right them to disk
		Properties p = PropertyManager.load(Paths.get("server.properties"));
		p.replace("level-type", "default", worldType.getName());
		p.replace("generator-settings", "", newGenSettings);
		props = new ServerProperties(p);
		props.save(Paths.get("server.properties"));
	}
}