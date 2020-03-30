package uk.co.harryyoud.biospheres;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;

@Mod(Biospheres.MODID)
public class Biospheres {

	public static final String MODID = "biospheres";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static final BiosphereWorldType worldType = new BiosphereWorldType();

	public Biospheres() {

	}
}
