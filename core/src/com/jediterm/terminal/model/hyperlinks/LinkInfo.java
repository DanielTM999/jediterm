package com.jediterm.terminal.model.hyperlinks;

import org.jetbrains.annotations.NotNull;

public class LinkInfo {
  private final Runnable myNavigateCallback;
  private final boolean myPreserveTextStyle;

  public LinkInfo(@NotNull Runnable navigateCallback) {
    this(navigateCallback, false);
  }

  public LinkInfo(@NotNull Runnable navigateCallback, boolean preserveTextStyle) {
    myNavigateCallback = navigateCallback;
    myPreserveTextStyle = preserveTextStyle;
  }

  /**
   * When {@code true}, highlighting this link keeps the colors the text already had instead of repainting it with
   * the hyperlink color, and the link is only underlined while hovered. Meant for links detected in output that is
   * already colored by the program writing it.
   */
  public boolean isPreserveTextStyle() {
    return myPreserveTextStyle;
  }

  public void navigate() {
    myNavigateCallback.run();
  }
}
