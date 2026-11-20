package com.poolsafety.frontend.theme;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** Text-level checks of the motion rules. They do not validate the visual rendering. */
class MotionCssPolicyTest {

    private static final Path THEME = Path.of("frontend/themes/poolsafety");
    private static String css;

    @BeforeAll
    static void load() throws Exception {
        css = Files.readString(THEME.resolve("animations.css"));
    }

    @Test
    void themeHasTheThreeRequiredStylesheets() {
        assertThat(THEME.resolve("styles.css")).exists();
        assertThat(THEME.resolve("animations.css")).exists();
        assertThat(THEME.resolve("responsive.css")).exists();
    }

    @Test
    void respectsReducedMotion() {
        assertThat(css).contains("@media (prefers-reduced-motion: reduce)");
        String reduced = css.substring(css.indexOf("@media (prefers-reduced-motion: reduce)"));
        assertThat(reduced).contains(".ps-skeleton::after").contains("animation: none");
    }

    @Test
    void onlyTheSkeletonShimmerRepeatsForever() {
        List<String> infinite = css.lines().filter(l -> l.contains("infinite")).toList();
        assertThat(infinite).hasSize(1);
        assertThat(infinite.get(0)).contains("ps-shimmer");
    }

    @Test
    void redPulseIsLimitedAndOnlyForActiveRedAlerts() {
        assertThat(css).contains(".ps-risk[data-level='RED'][data-active='true']");
        assertThat(Pattern.compile("animation:\\s*ps-red-pulse[^;]*\\s3;").matcher(css).find()).isTrue();
    }

    @Test
    void riskColorsTransitionSmoothly() {
        assertThat(css).contains("transition: background-color var(--ps-duration-slow)");
    }

    @Test
    void motionModuleExposesExactlyTheSpecifiedHelpers() throws Exception {
        String ts = Files.readString(Path.of("frontend/src/animations/motion.ts"));
        List<String> exported = Pattern.compile("export function (\\w+)").matcher(ts).results()
                .map(m -> m.group(1)).toList();
        assertThat(exported).containsExactlyInAnyOrder("animateViewEnter", "animateViewExit",
                "animateDialogOpen", "animateToast", "animateMetricChange", "animateCriticalAlert",
                "shakeInvalidField");
    }
}
