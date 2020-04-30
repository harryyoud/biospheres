package uk.co.harryyoud.biospheres.gui;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.google.common.base.Predicates;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.client.resources.I18n;

public class OptionBlock extends AbstractList.AbstractListEntry<OptionBlock> {

	private ScrollingOptionsList parent;
	private String title = "Config Option";
	private String validString = "";
	private String invalidString = "Invalid";
	private TextFieldWidget field;
	private boolean valid = true;
	private Predicate<String> validator = Predicates.alwaysTrue();
	private static final int RED = 16711680;
	private static final String CROSS_MARK = "\u274C";

	public OptionBlock(ScrollingOptionsList parent) {
		this.parent = parent;
	}

	public OptionBlock setField(TextFieldWidget field) {
		this.field = field;
		return this;
	}

	public OptionBlock setTitle(String title, Object... parameters) {
		this.title = I18n.format(title, parameters);
		return this;
	}

	public OptionBlock setInvalidString(String comment, Object... parameters) {
		this.invalidString = I18n.format(comment, parameters);
		return this;
	}

	public OptionBlock setInvalidString(Object[] args) {
		if (args.length < 1) {
			this.invalidString = "";
			return this;
		}
		String comment = (String) args[0];
		args[0] = CROSS_MARK;
		this.invalidString = I18n.format(comment, args);
		return this;
	}

	public OptionBlock setResponder(Consumer<String> f) {
		Consumer<String> f2 = (s) -> {
			this.valid = this.validator.test(s);
			f.accept(s);
		};
		this.field.setResponder(f2);
		return this;
	}

	public OptionBlock setText(String text) {
		if (this.field == null) {
			return this;
		}
		this.field.setText(text);
		return this;
	}

	public OptionBlock setMessage(String message) {
		if (this.field == null) {
			return this;
		}
		this.field.setMessage(message);
		return this;
	}

	public OptionBlock setValidator(Predicate<String> f) {
		this.validator = f;
		return this;
	}

	public OptionBlock setFieldValidator(Predicate<String> f) {
		this.field.setValidator(f);
		return this;
	}

	@Override
	public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_,
			int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
		this.parent.getParent().drawString(this.parent.getParent().getFont(), this.title,
				this.parent.getParent().width / 2 - 100, p_render_2_ + 5, -6250336);
		this.parent.getParent().drawString(this.parent.getParent().getFont(),
				this.valid ? this.validString : this.invalidString, this.parent.getParent().width / 2 - 100,
				p_render_2_ + 25 + 13 + 5, RED);
		if (this.field == null) {
			return;
		}
		this.field.y = p_render_2_ + 13 + 5;
		this.field.setFocused2(this.parent.getFocused() == this);
		this.field.render(p_render_2_, p_render_3_, p_render_9_);
	}

	public void tick() {
		if (this.field == null) {
			return;
		}
		// Make sure we blink the cursor
		this.field.tick();
	}

	// Delegate methods:
	// Pass IGuiEventListener methods to TextFieldWidget

	@Override
	public boolean changeFocus(boolean p_changeFocus_1_) {
		if (this.field == null) {
			return false;
		}
		return this.field.changeFocus(p_changeFocus_1_);
	}

	@Override
	public void mouseMoved(double xPos, double p_212927_3_) {
		if (this.field == null) {
			return;
		}
		this.field.mouseMoved(xPos, p_212927_3_);
	}

	@Override
	public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
		if (this.field == null) {
			return false;
		}
		return this.field.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
	}

	@Override
	public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
		if (this.field == null) {
			return false;
		}
		return this.field.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
	}

	@Override
	public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_,
			double p_mouseDragged_6_, double p_mouseDragged_8_) {
		if (this.field == null) {
			return false;
		}
		return this.field.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_,
				p_mouseDragged_8_);
	}

	@Override
	public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
		if (this.field == null) {
			return false;
		}
		return this.field.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_);
	}

	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
		if (this.field == null) {
			return false;
		}
		return this.field.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		if (this.field == null) {
			return false;
		}
		return this.field.keyReleased(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
		if (this.field == null) {
			return false;
		}
		return this.field.charTyped(p_charTyped_1_, p_charTyped_2_);
	}
}