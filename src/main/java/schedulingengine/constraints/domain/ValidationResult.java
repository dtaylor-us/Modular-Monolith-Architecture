package schedulingengine.constraints.domain;

import java.util.List;

/**
 * Result of constraint validation. Either valid (empty reasons) or invalid (one or more reasons).
 */
public record ValidationResult(
    boolean valid,
    List<String> reasons
) {
    public static ValidationResult passed() {
        return new ValidationResult(true, List.of());
    }

    public static ValidationResult failed(List<String> reasons) {
        if (reasons == null || reasons.isEmpty()) {
            throw new IllegalArgumentException("reasons must not be empty for invalid result");
        }
        return new ValidationResult(false, List.copyOf(reasons));
    }
}
