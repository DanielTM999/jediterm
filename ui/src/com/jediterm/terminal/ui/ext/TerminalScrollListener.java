package com.jediterm.terminal.ui.ext;

/**
 * Observes viewport movement, so callers can anchor the scroll position or preserve a selection while output keeps
 * coming in. Register with {@link com.jediterm.terminal.ui.TerminalPanel#addScrollListener(TerminalScrollListener)}.
 */
public interface TerminalScrollListener {

  default void beforeScrollArea(int scrollRegionTop, int scrollRegionSize, int dy) {
  }

  default void afterScrollArea(int scrollRegionTop, int scrollRegionSize, int dy) {
  }

  default void beforeMouseWheelScroll() {
  }

  /**
   * Called once the mouse wheel event was handled, whether or not it actually moved the viewport.
   */
  default void afterMouseWheelScroll() {
  }
}
