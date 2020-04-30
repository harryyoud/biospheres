package uk.co.harryyoud.biospheres.config.serializers;

public interface ISerializer<T> {
	public String serialize(T in);

	public T deserialize(String in);

	public boolean validate(String in);

	public default boolean validate(Object in) {
		if (!(in instanceof String)) {
			return false;
		}
		return this.validate((String) in);
	}

	// When typing, this does "validate(newText)", so to allow putting in empty
	// strings when typing in integers is useful, and to allow typing a string that
	// would start off invalid, and end up valid
	public default boolean validateField(String in) {
		return true;
	}

	public default Object[] getInvalidString() {
		return new Object[] { "biospheres.gui.default.label" };
	}
}
