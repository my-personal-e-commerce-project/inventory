package microservice.cloud.inventory.shared.domain.value_objects;

import com.github.slugify.Slugify;

public class Slug {

    private final String value;

    public Slug(String value) {
        this.value = value;
    }

    public static Slug create(String value) {
        Slugify slg = Slugify.builder().build();
        String result = slg.slugify(value);
        return new Slug(result);
    }

    public String value() {
        return value;
    }
}
