package com.jediterm.terminal.ui.ext;

import com.jediterm.terminal.TextStyle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Paints characters the terminal font has no glyph for using the first font that does.
 * <p>
 * Java2D only falls back automatically for logical fonts ({@code Dialog}, {@code Monospaced}, ...). A physical font
 * such as {@code Cascadia Mono} or {@code Consolas} paints an empty box for anything it is missing, which is what
 * modern CLI tools hit constantly with symbols like {@code U+23FA} or box drawing.
 * <p>
 * Lookups are cached per code point, so the cost after warm-up is a hash lookup per painted run.
 */
public class GlyphFallbackFontResolver implements TerminalFontResolver {

  /**
   * Ordered by how close the glyphs are to a terminal look. {@link Font#DIALOG} is last because it is a logical
   * font: it is always installed and carries the JDK's own fallback chain, so it is the catch-all.
   */
  public static final List<String> DEFAULT_FALLBACK_FAMILIES = List.of(
    "Segoe UI Symbol",
    "Segoe UI Emoji",
    "Noto Sans Symbols 2",
    "Noto Sans Symbols",
    "Noto Sans Mono",
    "DejaVu Sans",
    "Apple Symbols",
    "Apple Color Emoji",
    "Arial Unicode MS",
    Font.DIALOG
  );

  private static final String NO_FALLBACK = "";

  private final List<String> fallbackFamilies;
  private final Map<Integer, String> familyByCodePoint = new ConcurrentHashMap<>();
  private final Map<String, Font> probeFonts = new ConcurrentHashMap<>();
  private final Map<String, Font> derivedFonts = new ConcurrentHashMap<>();

  public GlyphFallbackFontResolver() {
    this(DEFAULT_FALLBACK_FAMILIES);
  }

  /**
   * @param candidateFamilies families to try, in order. Families that are not installed are dropped, except for
   *                          logical font names, which are always available.
   */
  public GlyphFallbackFontResolver(@NotNull Collection<String> candidateFamilies) {
    fallbackFamilies = retainInstalled(candidateFamilies);
  }

  @Override
  public @Nullable Font resolveFont(@NotNull Font base, char @NotNull [] text, int start, int end, @NotNull TextStyle style) {
    if (isPrintableAscii(text, start, end)) {
      return null;
    }
    int missingAt = base.canDisplayUpTo(text, start, end);
    if (missingAt < 0) {
      return null;
    }
    int codePoint = Character.codePointAt(text, missingAt, end);
    String family = familyByCodePoint.computeIfAbsent(codePoint, this::findFallbackFamily);
    if (NO_FALLBACK.equals(family)) {
      return null;
    }
    return deriveFont(family, base);
  }

  /**
   * A terminal font that cannot paint printable ASCII would be unusable anyway, so skipping the glyph check for it
   * keeps the common case out of the paint path entirely.
   */
  private static boolean isPrintableAscii(char[] text, int start, int end) {
    for (int i = start; i < end; i++) {
      char c = text[i];
      if (c < 0x20 || c > 0x7E) {
        return false;
      }
    }
    return true;
  }

  private @NotNull String findFallbackFamily(int codePoint) {
    for (String family : fallbackFamilies) {
      if (probeFont(family).canDisplay(codePoint)) {
        return family;
      }
    }
    return NO_FALLBACK;
  }

  private @NotNull Font probeFont(@NotNull String family) {
    return probeFonts.computeIfAbsent(family, name -> new Font(name, Font.PLAIN, 12));
  }

  private @NotNull Font deriveFont(@NotNull String family, @NotNull Font base) {
    String key = family + '|' + base.getStyle() + '|' + base.getSize();
    return derivedFonts.computeIfAbsent(key, ignored -> new Font(family, base.getStyle(), base.getSize()));
  }

  private static @NotNull List<String> retainInstalled(@NotNull Collection<String> candidateFamilies) {
    Set<String> installed = new HashSet<>();
    try {
      for (String name : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(Locale.ROOT)) {
        installed.add(name.toLowerCase(Locale.ROOT));
      }
    }
    catch (Throwable ignored) {
    }
    Set<String> logical = Set.of(Font.DIALOG, Font.DIALOG_INPUT, Font.SANS_SERIF, Font.SERIF, Font.MONOSPACED);
    List<String> result = new ArrayList<>(candidateFamilies.size());
    for (String family : candidateFamilies) {
      if (logical.contains(family) || installed.contains(family.toLowerCase(Locale.ROOT))) {
        result.add(family);
      }
    }
    return List.copyOf(result);
  }
}
