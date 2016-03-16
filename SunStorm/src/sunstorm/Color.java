package sunstorm;

public enum Color {
	RED    ("Red"),
	GREEN  ("Green"),
	BLUE   ("Blue"),
	WHITE  ("White"),
	BLACK  ("Black"),
	GRAY   ("Gray"),
	YELLOW ("Yellow"),
	NONE   ("None");
	
	private String name = "";
	   
	Color(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}

	public static boolean equiv(Color a, Color b) {
		if (a == b) {
			return true;
		}
		
		if ((a == RED && b == YELLOW) || (a == YELLOW && b == RED)) {
			return true;
		}
		
		if ((a == BLUE && b == GREEN) || (a == GREEN && b == BLUE )) {
			return true;
		}

		return false;
	}
}
