package com.maximebertheau.emoji;

/**
 * Created by Maxime Bertheau on 5/22/17.
 */
public enum Category {
    SYMBOLS,
    OBJECTS,
    NATURE,
    PEOPLE,
    FOODS,
    PLACES,
    ACTIVITY,
    FLAGS,
    ;

    public static Category parse(String input) {
        if(input == null) return null;

        input = input.toUpperCase();

        for (Category category : values()) {
            if (category.name().equals(input))
                return category;
        }

        return null;
    }
}
