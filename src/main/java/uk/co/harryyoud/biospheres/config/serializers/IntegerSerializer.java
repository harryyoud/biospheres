package uk.co.harryyoud.biospheres.config.serializers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class IntegerSerializer implements ISerializer<Integer> {
	private boolean inRangeTest = false;
	private boolean allowEmpty = false;
	private int min;
	private int max;
	List<Predicate<Integer>> extraValidators = new ArrayList<>();

	@Override
	public String serialize(Integer i) {
		return i.toString();
	}

	@Override
	public Integer deserialize(String s) {
		return Integer.parseInt(s);
	}

	@Override
	public boolean validate(String s) {
		if (s.isEmpty() && this.allowEmpty) {
			return true;
		}
		Integer i;
		try {
			i = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		for (Predicate<Integer> t : this.extraValidators) {
			if (!t.test(i)) {
				return false;
			}
		}
		return true;
	}

	public IntegerSerializer allowEmpty() {
		this.allowEmpty = true;
		return this;
	}

	public IntegerSerializer addInRange(int min, int max) {
		this.inRangeTest = true;
		this.min = min;
		this.max = max;
		this.extraValidators.add((i) -> {
			return i >= min && i <= max;
		});
		return this;
	}

	@Override
	public boolean validateField(String s) {
		if (s.isEmpty()) {
			return true;
		}
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public Object[] getInvalidString() {
		if (inRangeTest) {
			return new Object[] { "biospheres.gui.intRange.invalid", this.min, this.max };
		}
		return new Object[] { "biospheres.gui.int.invalid" };
	}
}
