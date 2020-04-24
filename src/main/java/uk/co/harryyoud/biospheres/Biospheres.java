package uk.co.harryyoud.biospheres;

import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.server.dedicated.ServerProperties;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Biospheres.MODID)
public class Biospheres {

	public static final String MODID = "biospheres";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static final BiosphereWorldType worldType = new BiosphereWorldType();

	public Biospheres() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::dedicatedServerSetup);
	}

	public void dedicatedServerSetup(FMLDedicatedServerSetupEvent event) {
		ServerProperties serverProperties = event.getServerSupplier().get().getServerProperties();

		System.out.println(String.format("Injecting biosphere world type into server.properties. Original value: %s",
				serverProperties.worldType.getName()));
		try {
			Class clz = ServerProperties.class;
			Field f = clz.getDeclaredField("worldType");
			f.setAccessible(true);
			f.set(serverProperties, worldType);
		} catch (Exception e) {
			throw new Error("ABORT, worldType not modifiable");
		}
	}

}
