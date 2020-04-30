package uk.co.harryyoud.biospheres.config.serializers;

import java.util.ArrayList;
import java.util.Map;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.block.BlockState;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.state.IProperty;
import net.minecraft.util.Util;

public class BlockStateSerializer implements ISerializer<BlockState> {
	@Override
	public String serialize(BlockState state) {
		String stateString = state.getBlock().getRegistryName().toString();
		final ArrayList<String> properties = new ArrayList<>();
		for (Map.Entry<IProperty<?>, Comparable<?>> entry : state.getValues().entrySet()) {
			final IProperty<?> property = entry.getKey();
			final Comparable<?> value = entry.getValue();
			properties.add(property.getName() + "=" + Util.getValueName(property, value));
		}
		if (!properties.isEmpty()) {
			stateString += "[";
			stateString += String.join(",", properties);
			stateString += "]";
		}
		return stateString;
	}

	@Override
	public BlockState deserialize(String s) {
		try {
			return new BlockStateArgument().parse(new StringReader(s)).getState();
		} catch (CommandSyntaxException e) {
			throw new IllegalArgumentException("Couldn't parse blockstate");
		}
	}

	@Override
	public boolean validate(String s) {
		if (s == null || s.isEmpty()) {
			return false;
		}
		try {
			new BlockStateArgument().parse(new StringReader(s));
			return true;
		} catch (CommandSyntaxException e) {
			return false;
		}
	}

	@Override
	public Object[] getInvalidString() {
		return new Object[] { "biospheres.gui.block.invalid" };
	}
}