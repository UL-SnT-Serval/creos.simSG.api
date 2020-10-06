package duc.sg.java.model;

public enum State {
    CLOSED("Closed"), OPEN("Open");

    private String name;

    State(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
