/**
 * Visual animation helpers based on the Web Animations API.
 * This module contains no business logic, no network calls, no role checks and no secrets.
 * Every helper is a no-op when the user prefers reduced motion.
 */

const reducedMotionQuery = window.matchMedia('(prefers-reduced-motion: reduce)');

function reducedMotion(): boolean {
  return reducedMotionQuery.matches;
}

function cssTime(name: string, fallbackMs: number): number {
  const raw = getComputedStyle(document.documentElement).getPropertyValue(name).trim();
  const value = parseFloat(raw);
  if (Number.isNaN(value)) {
    return fallbackMs;
  }
  return raw.endsWith('ms') ? value : value * 1000;
}

function cssEasing(): string {
  const raw = getComputedStyle(document.documentElement).getPropertyValue('--ps-ease').trim();
  return raw.length > 0 ? raw : 'ease-out';
}

export function animateViewEnter(element: HTMLElement): void {
  if (reducedMotion()) {
    return;
  }
  element.animate(
    [
      { opacity: 0, transform: 'translateY(8px)' },
      { opacity: 1, transform: 'none' },
    ],
    { duration: cssTime('--ps-duration-slow', 320), easing: cssEasing(), fill: 'backwards' },
  );
}

export function animateViewExit(element: HTMLElement): Promise<void> {
  if (reducedMotion()) {
    return Promise.resolve();
  }
  const animation = element.animate(
    [
      { opacity: 1, transform: 'none' },
      { opacity: 0, transform: 'translateY(-8px)' },
    ],
    { duration: cssTime('--ps-duration', 200), easing: cssEasing(), fill: 'forwards' },
  );
  return animation.finished.then(() => undefined);
}

export function animateDialogOpen(element: HTMLElement): void {
  if (reducedMotion()) {
    return;
  }
  element.animate(
    [
      { opacity: 0, transform: 'translateY(12px) scale(0.98)' },
      { opacity: 1, transform: 'none' },
    ],
    { duration: cssTime('--ps-duration-slow', 320), easing: cssEasing(), fill: 'backwards' },
  );
}

export function animateToast(element: HTMLElement): void {
  if (reducedMotion()) {
    return;
  }
  element.animate(
    [
      { opacity: 0, transform: 'translateY(12px)' },
      { opacity: 1, transform: 'none' },
    ],
    { duration: cssTime('--ps-duration', 200), easing: cssEasing(), fill: 'backwards' },
  );
}

/** Brief highlight when a displayed number changes. */
export function animateMetricChange(element: HTMLElement): void {
  if (reducedMotion()) {
    return;
  }
  element.animate(
    [
      { transform: 'scale(1)', opacity: 0.6 },
      { transform: 'scale(1.06)', opacity: 1 },
      { transform: 'scale(1)', opacity: 1 },
    ],
    { duration: cssTime('--ps-duration-slow', 320), easing: cssEasing() },
  );
}

/** Limited, subtle pulse for an active RED alert: 3 iterations, then it stops. */
export function animateCriticalAlert(element: HTMLElement): void {
  if (reducedMotion()) {
    return;
  }
  element.animate(
    [
      { boxShadow: '0 0 0 0 rgba(239, 83, 80, 0.5)' },
      { boxShadow: '0 0 0 10px rgba(239, 83, 80, 0)' },
    ],
    { duration: 1400, easing: 'ease-out', iterations: 3 },
  );
}

export function shakeInvalidField(element: HTMLElement): void {
  if (reducedMotion()) {
    return;
  }
  element.animate(
    [
      { transform: 'translateX(0)' },
      { transform: 'translateX(-6px)' },
      { transform: 'translateX(6px)' },
      { transform: 'translateX(-3px)' },
      { transform: 'translateX(0)' },
    ],
    { duration: 280, easing: 'ease-in-out' },
  );
}

declare global {
  interface Window {
    poolSafetyMotion: {
      animateViewEnter: typeof animateViewEnter;
      animateViewExit: typeof animateViewExit;
      animateDialogOpen: typeof animateDialogOpen;
      animateToast: typeof animateToast;
      animateMetricChange: typeof animateMetricChange;
      animateCriticalAlert: typeof animateCriticalAlert;
      shakeInvalidField: typeof shakeInvalidField;
    };
  }
}

// Exposed so the Java side can trigger purely visual effects on a component's element
window.poolSafetyMotion = {
  animateViewEnter,
  animateViewExit,
  animateDialogOpen,
  animateToast,
  animateMetricChange,
  animateCriticalAlert,
  shakeInvalidField,
};
