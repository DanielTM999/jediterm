package com.jediterm.terminal.ui.ext;

import com.jediterm.terminal.TextStyle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Font;

/**
 * Chooses the font used to paint a run of characters, without requiring a {@code TerminalPanel} subclass.
 * <p>
 * Register one with {@link com.jediterm.terminal.ui.TerminalPanel#setFontResolver(TerminalFontResolver)}. It is
 * consulted for every painted run, so implementations must be cheap and thread-safe; see
 * {@link GlyphFallbackFontResolver} for a cached implementation.
 */
public interface TerminalFontResolver {

  /**
   * @param base  the font the panel would use, already resolved for bold/italic
   * @param text  the buffer being painted
   * @param start inclusive start of the run inside {@code text}
   * @param end   exclusive end of the run inside {@code text}
   * @param style the style of the run
   * @return the font to paint the run with, or {@code null} to keep {@code base}
   */
  @Nullable Font resolveFont(@NotNull Font base, char @NotNull [] text, int start, int end, @NotNull TextStyle style);
}
