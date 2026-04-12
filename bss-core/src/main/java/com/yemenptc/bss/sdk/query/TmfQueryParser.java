package com.yemenptc.bss.sdk.query;

import jakarta.persistence.criteria.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses TMF query parameters (?fields=, ?filter=, ?sort=) into JPA Criteria queries.
 * Supports:
 * - ?fields=id,name,status (field selection)
 * - ?filter=status/eq/active (basic filtering)
 * - ?filter=characteristics[name/eq/FTTH] (nested JSONB filtering)
 * - ?sort=createdAt:desc,name:asc (sorting)
 * - ?limit=20&offset=0 (pagination)
 */
public class TmfQueryParser<T> {

    private static final Pattern FILTER_PATTERN = Pattern.compile("([\\w.]+)/(\\w+)/([\\w\\s.:-]+)");
    private static final Pattern NESTED_FILTER_PATTERN = Pattern.compile("([\\w.]+)\\[([\\w.]+)/(\\w+)/([\\w\\s.:-]+)\\]");
    private static final Pattern SORT_PATTERN = Pattern.compile("([\\w.]+):(asc|desc)");

    /**
     * Parses ?filter= parameter into JPA Specification.
     * Example: ?filter=status/eq/active&filter=customerType/eq/BUSINESS
     */
    public Specification<T> parseFilters(List<String> filters) {
        if (filters == null || filters.isEmpty()) {
            return Specification.where(null);
        }

        Specification<T> spec = Specification.where(null);

        for (String filter : filters) {
            Matcher nestedMatcher = NESTED_FILTER_PATTERN.matcher(filter);
            if (nestedMatcher.matches()) {
                String field = nestedMatcher.group(1);
                String nestedField = nestedMatcher.group(2);
                String operator = nestedMatcher.group(3);
                String value = nestedMatcher.group(4);
                spec = spec.and(createNestedSpecification(field, nestedField, operator, value));
            } else {
                Matcher matcher = FILTER_PATTERN.matcher(filter);
                if (matcher.matches()) {
                    String field = matcher.group(1);
                    String operator = matcher.group(2);
                    String value = matcher.group(3);
                    spec = spec.and(createSpecification(field, operator, value));
                }
            }
        }

        return spec;
    }

    /**
     * Parses ?sort= parameter into Pageable.
     * Example: ?sort=createdAt:desc,name:asc&limit=20&offset=0
     */
    public Pageable parsePagination(List<String> sorts, Integer limit, Integer offset) {
        List<Sort.Order> orders = new ArrayList<>();

        if (sorts != null) {
            for (String sort : sorts) {
                Matcher matcher = SORT_PATTERN.matcher(sort);
                if (matcher.matches()) {
                    String field = matcher.group(1);
                    String direction = matcher.group(2);
                    orders.add(new Sort.Order(
                            "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC,
                            field
                    ));
                }
            }
        }

        Sort sort = orders.isEmpty() ? Sort.unsorted() : Sort.by(orders);
        int page = (offset != null && limit != null) ? offset / limit : 0;
        int size = limit != null ? limit : 20;

        return PageRequest.of(page, size, sort);
    }

    /**
     * Parses ?fields= parameter for field selection.
     * Returns list of field names to include in response.
     */
    public List<String> parseFields(String fieldsParam) {
        if (fieldsParam == null || fieldsParam.isBlank()) {
            return List.of();
        }
        return List.of(fieldsParam.split(","));
    }

    private Specification<T> createSpecification(String field, String operator, String value) {
        return (root, query, cb) -> {
            Path<?> path = getPath(root, field);

            return switch (operator.toLowerCase()) {
                case "eq" -> cb.equal(path, parseValue(path, value));
                case "neq" -> cb.notEqual(path, parseValue(path, value));
                case "gt" -> cb.greaterThan(path.as(String.class), value);
                case "gte" -> cb.greaterThanOrEqualTo(path.as(String.class), value);
                case "lt" -> cb.lessThan(path.as(String.class), value);
                case "lte" -> cb.lessThanOrEqualTo(path.as(String.class), value);
                case "like" -> cb.like(path.as(String.class), "%" + value + "%");
                case "in" -> {
                    String[] values = value.split(",");
                    CriteriaBuilder.In<Object> in = cb.in(path);
                    for (String v : values) {
                        in.value(parseValue(path, v.trim()));
                    }
                    yield in;
                }
                case "isnull" -> cb.isNull(path);
                case "isnotnull" -> cb.isNotNull(path);
                default -> cb.equal(path, parseValue(path, value));
            };
        };
    }

    private Specification<T> createNestedSpecification(String field, String nestedField, String operator, String value) {
        return (root, query, cb) -> {
            Expression<String> jsonField = cb.function(
                    "jsonb_extract_path_text",
                    String.class,
                    root.get(field),
                    cb.literal(nestedField)
            );

            return switch (operator.toLowerCase()) {
                case "eq" -> cb.equal(jsonField, value);
                case "neq" -> cb.notEqual(jsonField, value);
                case "like" -> cb.like(jsonField, "%" + value + "%");
                default -> cb.equal(jsonField, value);
            };
        };
    }

    @SuppressWarnings("unchecked")
    private <Y> Path<Y> getPath(Root<T> root, String field) {
        String[] parts = field.split("\\.");
        Path<?> path = root;
        for (String part : parts) {
            path = path.get(part);
        }
        return (Path<Y>) path;
    }

    private Object parseValue(Path<?> path, String value) {
        Class<?> javaType = path.getJavaType();

        if (javaType == Integer.class || javaType == int.class) {
            return Integer.parseInt(value);
        } else if (javaType == Long.class || javaType == long.class) {
            return Long.parseLong(value);
        } else if (javaType == Boolean.class || javaType == boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (javaType == Instant.class) {
            return Instant.parse(value);
        } else if (javaType == LocalDate.class) {
            return LocalDate.parse(value);
        } else if (javaType.isEnum()) {
            return Enum.valueOf((Class<Enum>) javaType, value.toUpperCase());
        }

        return value;
    }
}
