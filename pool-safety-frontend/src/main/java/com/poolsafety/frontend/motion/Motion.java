package com.poolsafety.frontend.motion;

import com.vaadin.flow.component.Component;

/**
 * Thin Java entry point to the visual helpers in frontend/src/animations/motion.ts.
 * Function names are fixed constants; no user input is ever concatenated into the script.
 */
public final class Motion {

    private Motion() {
    }

    public static void animateViewEnter(Component component) {
        run(component, "animateViewEnter");
    }

    public static void animateMetricChange(Component component) {
        run(component, "animateMetricChange");
    }

    public static void animateCriticalAlert(Component component) {
        run(component, "animateCriticalAlert");
    }

    public static void shakeInvalidField(Component component) {
        run(component, "shakeInvalidField");
    }

    private static void run(Component component, String helperName) {
        component.getElement().executeJs("window.poolSafetyMotion." + helperName + "(this)");
    }
}
