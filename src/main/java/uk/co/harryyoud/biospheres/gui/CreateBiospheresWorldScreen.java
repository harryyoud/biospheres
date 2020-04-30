package uk.co.harryyoud.biospheres.gui;

import java.util.Map;

import com.mojang.datafixers.Dynamic;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.text.TranslationTextComponent;
import uk.co.harryyoud.biospheres.config.BiosphereGenSettingsSerializer;
import uk.co.harryyoud.biospheres.config.serializers.ISerializer;

public class CreateBiospheresWorldScreen extends Screen {
	private BiosphereGenSettingsSerializer generatorInfo = new BiosphereGenSettingsSerializer();
	private final CreateWorldScreen createWorldGui;
	private ScrollingOptionsList optionsList;

	public CreateBiospheresWorldScreen(CreateWorldScreen parent, CompoundNBT generatorOptions) {
		super(new TranslationTextComponent("biospheres.gui.title"));
		this.createWorldGui = parent;
		this.loadGeneratorOptions(generatorOptions);
	}

	public CompoundNBT getGeneratorOptions() {
		return (CompoundNBT) this.generatorInfo.toNBT(NBTDynamicOps.INSTANCE).getValue();
	}

	public void loadGeneratorOptions(CompoundNBT nbt) {
		this.generatorInfo = BiosphereGenSettingsSerializer.fromNBT(new Dynamic<>(NBTDynamicOps.INSTANCE, nbt));
	}

	@Override
	protected void init() {
		this.optionsList = new ScrollingOptionsList(this, this.minecraft, this.width, this.height);
		this.children.add(this.optionsList);
		this.initOptions();
		this.addButton(
				new Button(this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("gui.done"), (p_213010_1_) -> {
					this.createWorldGui.chunkProviderSettingsJson = this.getGeneratorOptions();
					this.minecraft.displayGuiScreen(this.createWorldGui);
				}));
		this.addButton(
				new Button(this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel"), (p_213009_1_) -> {
					this.minecraft.displayGuiScreen(this.createWorldGui);
				}));
	}

	protected void initOptions() {
		for (Map.Entry<String, ISerializer<?>> entry : this.generatorInfo.getSerializers().entrySet()) {
			String key = entry.getKey();
			String label = "biospheres.gui." + key + ".label";
			//@formatter:off
			this.optionsList.create()
					.setTitle(label)
					.setMessage(label)
					.setText(this.generatorInfo.get(key))
					.setInvalidString(this.generatorInfo.getSerializer(key).getInvalidString())
					.setResponder((s) -> this.generatorInfo.set(key, s))
					.setValidator(this.generatorInfo.getValidator(key))
					.setFieldValidator(this.generatorInfo.getSerializer(key)::validateField);
			//@formatter:on
		}
	}

	@Override
	public void onClose() {
		this.minecraft.displayGuiScreen(this.createWorldGui);
	}

	@Override
	public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
		this.renderBackground();
		this.optionsList.render(p_render_1_, p_render_2_, p_render_3_);
		this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 20, 16777215);
		super.render(p_render_1_, p_render_2_, p_render_3_);
	}

	FontRenderer getFont() {
		return this.font;
	}

	@Override
	public void tick() {
		this.optionsList.tick();
	}

}
