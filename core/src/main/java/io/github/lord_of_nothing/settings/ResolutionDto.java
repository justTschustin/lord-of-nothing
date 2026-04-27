package io.github.lord_of_nothing.settings;

public class ResolutionDto {
    public int width;
    public int height;

    public ResolutionDto(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public String toString() {
        return width + "x" + height;
    }
}
