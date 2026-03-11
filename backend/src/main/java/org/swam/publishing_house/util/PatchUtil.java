package org.swam.publishing_house.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiConsumer;

public class PatchUtil {

    /**
     * Generic patch method that copies non-null fields from source to target
     */
    public static <S, T> void applyPatch(S source, T target, Set<String> excludeFields) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Source and target cannot be null");
        }

        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();

        // Get all fields from source
        Field[] sourceFields = getAllFields(sourceClass);

        for (Field sourceField : sourceFields) {
            String fieldName = sourceField.getName();

            // Skip excluded fields, static fields, and class field
            if (excludeFields.contains(fieldName) ||
                    java.lang.reflect.Modifier.isStatic(sourceField.getModifiers()) ||
                    fieldName.equals("class")) {
                continue;
            }

            try {
                sourceField.setAccessible(true);
                Object value = sourceField.get(source);

                // Only update if value is not null
                if (value != null) {
                    Field targetField = findField(targetClass, fieldName);
                    if (targetField != null) {
                        targetField.setAccessible(true);
                        targetField.set(target, value);
                    }
                }
            } catch (IllegalAccessException e) {
                // Log warning and continue
                System.err.println("Warning: Could not access field " + fieldName + ": " + e.getMessage());
            }
        }
    }

    /**
     * Overloaded method with no excluded fields
     */
    public static <S, T> void applyPatch(S source, T target) {
        applyPatch(source, target, Collections.emptySet());
    }

    /**
     * Generic patch with validation
     */
    public static <S, T> void applyPatchWithValidation(S source, T target,
                                                       Set<String> excludeFields,
                                                       Validator validator) {
        applyPatch(source, target, excludeFields);

        if (validator != null) {
            Set<ConstraintViolation<T>> violations = validator.validate(target);
            if (!violations.isEmpty()) {
                StringBuilder errorMessage = new StringBuilder("Validation failed: ");
                violations.forEach(v -> errorMessage.append(v.getMessage()).append("; "));
                throw new RuntimeException(errorMessage.toString());
            }
        }
    }

    /**
     * Generic patch with custom field handlers
     */
    public static <S, T> PatchBuilder<S, T> patch(S source, T target) {
        return new PatchBuilder<>(source, target);
    }

    // Helper methods
    private static Field[] getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }

    private static Field findField(Class<?> clazz, String fieldName) {
        while (clazz != null && clazz != Object.class) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    /**
     * Builder pattern for complex patch operations
     */
    public static class PatchBuilder<S, T> {
        private final S source;
        private final T target;
        private final Set<String> excludeFields = new HashSet<>();
        private final Map<String, BiConsumer<S, T>> customHandlers = new HashMap<>();
        private Validator validator;

        public PatchBuilder(S source, T target) {
            this.source = source;
            this.target = target;
        }

        public PatchBuilder<S, T> exclude(String... fields) {
            excludeFields.addAll(Arrays.asList(fields));
            return this;
        }

        public PatchBuilder<S, T> handle(String fieldName, BiConsumer<S, T> handler) {
            customHandlers.put(fieldName, handler);
            excludeFields.add(fieldName); // Exclude from default processing
            return this;
        }

        public PatchBuilder<S, T> validate(Validator validator) {
            this.validator = validator;
            return this;
        }

        public T apply() {
            // Apply default patch
            PatchUtil.applyPatch(source, target, excludeFields);

            // Apply custom handlers
            customHandlers.forEach((fieldName, handler) -> handler.accept(source, target));

            // Apply validation if provided
            if (validator != null) {
                Set<ConstraintViolation<T>> violations = validator.validate(target);
                if (!violations.isEmpty()) {
                    StringBuilder errorMessage = new StringBuilder("Validation failed: ");
                    violations.forEach(v -> errorMessage.append(v.getMessage()).append("; "));
                    throw new RuntimeException(errorMessage.toString());
                }
            }

            return target;
        }
    }
}