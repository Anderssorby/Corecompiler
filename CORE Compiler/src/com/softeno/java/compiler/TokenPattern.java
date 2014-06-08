package com.softeno.java.compiler;

public abstract class TokenPattern implements PatternComponent {

	public static final int TOKEN_APROVED = 0x0;

	public static final int TOKEN_ILLEGAL = 0x1;

	public static final int PATTERN_ENDED = 0x2;

	protected int lookup;

	public static class PatternOR extends TokenPattern {
		private PatternComponent[] components;

		private PatternOR(PatternComponent[] components) {
			this.components = components;

		}

		@Override
		public boolean recognise(Symbol symbol, int point) {
			lookup = -1;
			for (int i = 0; i < components.length; i++) {
				PatternComponent c = components[i];
				if (c.recognise(symbol, point)) {
					lookup = i;
					return true;
				}
			}
			return false;
		}

		@Override
		public PatternComponent getLookUp() {
			return components[lookup];
		}
	}

	public static class PatternAND extends TokenPattern {
		private PatternComponent[] components;

		private PatternAND(PatternComponent[] components) {
			this.components = components;

		}

		@Override
		public boolean recognise(Symbol symbol, int point) {
			lookup = point;
			return components[point].recognise(symbol, 0);
		}

		@Override
		public PatternComponent getLookUp() {
			return components[lookup];
		}
	}

	public static PatternOR or(PatternComponent... components) {
		return new PatternOR(components);
	}

	public static PatternAND and(PatternComponent... components) {
		return new PatternAND(components);
	}

	public abstract PatternComponent getLookUp();

}
