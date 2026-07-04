package microservice.cloud.inventory.shared.domain.value_objects;

import com.github.slugify.Slugify;

public class Slug {
    private final String value;

    private Slug(String value) {
        if(value == null)
            throw new RuntimeException("The slug cannot be null");

        this.value = value;
    }

    public static Slug fromString(String value) {
        return new Slug(value);
    }

    public static Slug create(String value) {
        Slugify slg = Slugify.builder().build();
        String result = slg.slugify(value);
        return new Slug(result);
    }

    public String value() {
        return value;
    }

    public boolean equals(Slug slug){
        if (slug == null) return false;
        return value.equals(slug.value());
    }
}
