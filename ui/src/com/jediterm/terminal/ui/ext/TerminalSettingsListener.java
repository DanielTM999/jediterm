package com.jediterm.terminal.ui.ext;

import com.jediterm.terminal.ui.settings.SettingsProvider;
import org.jetbrains.annotations.NotNull;

/**
 * Notified when the settings of a terminal changed, either because a new {@link SettingsProvider} was installed or
 * because the current one reported new values. Register with
 * {@link com.jediterm.terminal.ui.TerminalPanel#addSettingsListener(TerminalSettingsListener)}.
 */
public interface TerminalSettingsListener {

  void settingsChanged(@NotNull SettingsProvider provider);
}
