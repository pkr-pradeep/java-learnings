package org.example.utilities;

import org.example.domain.interfaces.PII;

import java.lang.reflect.Field;

public abstract class PiiSafeToString {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Class<?> clazz = this.getClass();
        sb.append(clazz.getSimpleName()).append("{");

        Field[] fields = clazz.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);

            try {
                sb.append(field.getName()).append("=");

                if (field.isAnnotationPresent(PII.class)) {
                    PII pii = field.getAnnotation(PII.class);
                    sb.append(pii.mask());         // mask value
                } else {
                    sb.append(field.get(this));     // print normally
                }

            } catch (Exception e) {
                sb.append("<?>");
            }

            if (i < fields.length - 1) sb.append(", ");
        }

        sb.append("}");
        return sb.toString();
    }
}
