package schedulingengine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that the application module structure adheres to Spring Modulith rules:
 * - No cycles between modules
 * - No access to other modules' internal packages (only public API)
 * - Explicit allowedDependencies are respected
 */
@DisplayName("Modulith boundaries")
class ModulithBoundariesTest {

    private static final ApplicationModules MODULES = ApplicationModules.of(Application.class);

    @Test
    @DisplayName("module arrangement verifies")
    void moduleArrangementVerifies() {
        MODULES.verify();
    }

    @Test
    @DisplayName("expected modules are present")
    void expectedModulesPresent() {
        assertThat(MODULES.getModuleByName("scheduling")).isPresent();
        assertThat(MODULES.getModuleByName("constraints")).isPresent();
        assertThat(MODULES.getModuleByName("optimization")).isPresent();
        assertThat(MODULES.getModuleByName("notifications")).isPresent();
    }
}
