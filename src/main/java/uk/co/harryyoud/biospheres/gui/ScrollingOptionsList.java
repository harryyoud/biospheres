package uk.co.harryyoud.biospheres.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.INestedGuiEventHandler;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.list.AbstractList;

class ScrollingOptionsList extends AbstractList<OptionBlock> implements INestedGuiEventHandler {
	private CreateBiospheresWorldScreen parent;

	public ScrollingOptionsList(CreateBiospheresWorldScreen parent, Minecraft mc, int parentWidth, int parentHeight) {
		super(mc, parentWidth, parentHeight, 43, parentHeight - 40, 60);
		this.parent = parent;
	}

	public CreateBiospheresWorldScreen getParent() {
		return this.parent;
	}

	public OptionBlock create() {
		OptionBlock block = new OptionBlock(this);
		int y = (this.getItemCount() + 1) * this.itemHeight;
		TextFieldWidget field = new TextFieldWidget(parent.getFont(), this.width / 2 - 100, y, 200, 20, "");
		block.setField(field);
		this.addEntry(block);
		return block;
	}

	@Override
	public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
		super.render(p_render_1_, p_render_2_, p_render_3_);
	}

	public void tick() {
		for (OptionBlock opt : this.children()) {
			opt.tick();
		}
	}

	@Override
	public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
		if (!this.isMouseOver(p_mouseClicked_1_, p_mouseClicked_3_)
				|| this.getEntryAtPosition(p_mouseClicked_1_, p_mouseClicked_3_) == null) {
			this.setFocused(null);
		}
		return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
	}
}
