package com.jediterm.terminal.ui.ext;

import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;

/**
 * Observes key events on their way into the terminal, with the ability to consume them.
 * <p>
 * This is what {@code addCustomKeyListener} cannot do: a plain {@code KeyListener} sees the event but cannot stop
 * it from reaching the emulator. Register with
 * {@link com.jediterm.terminal.ui.TerminalPanel#addKeyInterceptor(TerminalKeyInterceptor)}.
 */
public interface TerminalKeyInterceptor {

  /**
   * @return {@code true} to consume the event, so neither the custom key listeners nor the emulator see it
   */
  default boolean beforeKeyEvent(@NotNull KeyEvent e) {
    return false;
  }

  /**
   * Called after the event was handled, and only when it was not consumed.
   */
  default void afterKeyEvent(@NotNull KeyEvent e) {
  }
}
