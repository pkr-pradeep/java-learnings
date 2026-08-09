package org.example.utilities;

import org.example.domain.interfaces.PII;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class providing automatic reflection-based PII masking for toString().
 */
public abstract class PiiSafeToString {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Class<?> clazz = this.getClass();
        sb.append(clazz.getSimpleName()).append("{");

        List<Field> fields = getAllFields(clazz);

        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            field.setAccessible(true);

            try {
                sb.append(field.getName()).append("=");

                if (field.isAnnotationPresent(PII.class)) {
                    PII pii = field.getAnnotation(PII.class);
                    sb.append(pii.mask());
                } else {
                    Object val = field.get(this);
                    sb.append(val != null ? val.toString() : "null");
                }

            } catch (IllegalAccessException e) {
                sb.append("<ACCESS_DENIED>");
            } catch (Exception e) {
                sb.append("<?>");
            }

            if (i < fields.size() - 1) {
                sb.append(", ");
            }
        }

        sb.append("}");
        return sb.toString();
    }

    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        Class<?> current = clazz;
        while (current != null && current != Object.class && current != PiiSafeToString.class) {
            for (Field f : current.getDeclaredFields()) {
                fieldList.add(f);
            }
            current = current.getSuperclass();
        }
        return fieldList;
    }
}

