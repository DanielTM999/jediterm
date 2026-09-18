package com.jediterm.terminal.ui.settings;

import com.jediterm.terminal.CursorShape;
import com.jediterm.terminal.HyperlinkStyle;
import com.jediterm.terminal.TerminalColor;
import com.jediterm.terminal.TextStyle;
import com.jediterm.terminal.emulator.ColorPalette;
import com.jediterm.terminal.model.TerminalTypeAheadSettings;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;

public interface UserSettingsProvider {
  ColorPalette getTerminalColorPalette();

  Font getTerminalFont();

  float getTerminalFontSize();

  /**
   * @return vertical scaling factor
   */
  default float getLineSpacing() {
    return 1.0f;
  }

  default boolean shouldDisableLineSpacingForAlternateScreenBuffer() {
    return false;
  }

  default boolean shouldFillCharacterBackgroundIncludingLineSpacing() {
    return true;
  }

  default @NotNull TerminalColor getDefaultForeground() {
    return Objects.requireNonNull(getDefaultStyle().getForeground());
  }

  default @NotNull TerminalColor getDefaultBackground() {
    return Objects.requireNonNull(getDefaultStyle().getBackground());
  }

  /**
   * @deprecated override {@link UserSettingsProvider#getDefaultForeground()} and
   * {@link UserSettingsProvider#getDefaultBackground()} instead
   */
  @Deprecated
  default @NotNull TextStyle getDefaultStyle() {
    return new TextStyle(TerminalColor.BLACK, TerminalColor.WHITE);
  }

  @NotNull TextStyle getSelectionColor();

  @NotNull TextStyle getFoundPatternColor();

  TextStyle getHyperlinkColor();

  HyperlinkStyle.HighlightMode getHyperlinkHighlightingMode();

  /**
   * How much of the foreground color survives when a run carries the DIM (SGR 2) attribute, as a fraction between
   * 0 and 1: {@code 1} paints it at full strength, {@code 0.5} is the classic even blend with the background.
   * <p>
   * Modern CLI tools lean on DIM for secondary text, and an even blend can push it close to unreadable against a
   * high-contrast background, so this is worth raising for a terminal embedded in an IDE.
   */
  default float dimIntensity() { return 0.5f; }

  /**
   * Shape of the caret while the running application has not asked for one via DECSCUSR. An application's own
   * request always wins over this.
   */
  default @NotNull CursorShape getDefaultCursorShape() { return CursorShape.BLINK_BLOCK; }

  /**
   * Extended modifier mask (see {@link java.awt.event.InputEvent}) that must be held down for a hyperlink to be
   * highlighted and activated. {@code 0}, the default, keeps the upstream behaviour of a plain click. A link may
   * override this per instance through {@code LinkInfoEx}.
   */
  default int getLinkActivationModifiersEx() { return 0; }

  default boolean enableTextBlinking() { return false; }

  default int slowTextBlinkMs() { return 1000; }

  default int rapidTextBlinkMs() { return 500; }

  boolean useInverseSelectionColor();

  boolean copyOnSelect();

  boolean pasteOnMiddleMouseClick();

  boolean emulateX11CopyPaste();

  boolean useAntialiasing();

  int maxRefreshRate();

  boolean audibleBell();

  boolean enableMouseReporting();

  int caretBlinkingMs();

  boolean scrollToBottomOnTyping();

  boolean DECCompatibilityMode();

  boolean forceActionOnMouseReporting();

  int getBufferMaxLinesCount();
  
  boolean altSendsEscape();

  boolean ambiguousCharsAreDoubleWidth();

  @NotNull TerminalTypeAheadSettings getTypeAheadSettings();

  default boolean simulateMouseScrollWithArrowKeysInAlternativeScreen() {
    return sendArrowKeysInAlternativeMode();
  }

  @SuppressWarnings("DeprecatedIsStillUsed")
  @Deprecated(forRemoval = true)
  default boolean sendArrowKeysInAlternativeMode() {
    return true;
  }

  /**
   * By default, if you press Shift+Enter in the terminal, it will just send CR (the same as for Enter with no modifier)
   * because the VT format knows nothing about Shift modifier.
   * Though, from the user perspective, Shift+Enter should behave differently in CLI apps, for example, insert a new line.
   * So, if this option is enabled, Shift+Enter will send Esc+CR instead of just CR.
   */
  default boolean shiftEnterSendsEscCR() {
    return false;
  }
}
