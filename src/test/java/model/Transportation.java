package model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
public class Transportation {
    private String name;
    private Integer quantity;
    private String brand;
    private Category category;

    @Data
    @AllArgsConstructor
    public static class Category {
        private int id;
        private String name;

        public static final Category VEHICLE = new Category(1, "Vehicle");
        public static final Category SHIP = new Category(2, "Ship");
        public static final Category PLANE = new Category(3, "Plane");
    }

    public static final List<Transportation> SAMPLE = Arrays.asList(
            new Transportation("A1", 12, "Huyndai", Category.VEHICLE),
            new Transportation("B1", 10, "Huyndai", Category.VEHICLE),
            new Transportation("A2", 19, "Audi", Category.VEHICLE),
            new Transportation("B2", 6, "Audi", Category.VEHICLE),
            new Transportation("C2", 6, "Audi", Category.VEHICLE),
            new Transportation("S2", 10, "Evergreen", Category.SHIP),
            new Transportation("S3", 14, "Evergreen", Category.SHIP),
            new Transportation("S1", 8, "Maersk", Category.SHIP)
            );
}
