package onetomany.Users;

public class ColorUpdateRequest {
    private String backgroundColor; // Expecting the color as a hex string (e.g., "ff88b070")

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}