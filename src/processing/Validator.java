package processing;

import validation.NotBlank;
import validation.NotNull;
import validation.Range;

import java.lang.reflect.Field;

public class Validator {

    public static void validate(Object obj)
            throws ValidationException, IllegalAccessException {

        if (obj == null) {
            throw new ValidationException("Object to validate is null");
        }

        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(obj);

            // @NotNull
            if (field.isAnnotationPresent(NotNull.class)) {
                if (value == null) {
                    throw new ValidationException(
                            "Field '" + field.getName() + "' must not be null");
                }
            }

            // @NotBlank
            if (field.isAnnotationPresent(NotBlank.class)) {
                if (value == null || value.toString().trim().isEmpty()) {
                    throw new ValidationException(
                            "Field '" + field.getName() + "' must not be blank");
                }
            }

            // @Range
            if (field.isAnnotationPresent(Range.class)) {
                Range range = field.getAnnotation(Range.class);

                if (value == null) {
                    // boşsa ve @NotNull yoksa, range kontrol etmiyoruz
                    continue;
                }

                if (value instanceof Number number) {
                    long v = number.longValue();
                    if (v < range.min() || v > range.max()) {
                        throw new ValidationException(
                                "Field '" + field.getName()
                                        + "' is out of range [" + range.min()
                                        + ", " + range.max() + "], value=" + v);
                    }
                } else {
                    throw new ValidationException(
                            "Field '" + field.getName()
                                    + "' annotated with @Range is not numeric");
                }
            }
        }
    }
}
