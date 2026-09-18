package com.jediterm.terminal.ui.hyperlinks;

import com.jediterm.terminal.model.hyperlinks.LinkInfo;
import com.jediterm.terminal.ui.TerminalAction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

public final class LinkInfoEx extends LinkInfo {

  private final PopupMenuGroupProvider myPopupMenuGroupProvider;
  private final HoverConsumer myHoverConsumer;
  private final Integer myActivationModifiersEx;

  public LinkInfoEx(@NotNull Runnable navigateCallback) {
    this(navigateCallback, null, null, null, false);
  }

  private LinkInfoEx(@NotNull Runnable navigateCallback,
                     @Nullable PopupMenuGroupProvider popupMenuGroupProvider,
                     @Nullable HoverConsumer hoverConsumer,
                     @Nullable Integer activationModifiersEx,
                     boolean preserveTextStyle) {
    super(navigateCallback, preserveTextStyle);
    myPopupMenuGroupProvider = popupMenuGroupProvider;
    myHoverConsumer = hoverConsumer;
    myActivationModifiersEx = activationModifiersEx;
  }

  public @Nullable PopupMenuGroupProvider getPopupMenuGroupProvider() {
    return myPopupMenuGroupProvider;
  }

  public @Nullable HoverConsumer getHoverConsumer() {
    return myHoverConsumer;
  }

  /**
   * Extended modifier mask that must be held down for this link to be highlighted and activated, or {@code null}
   * to fall back to {@link com.jediterm.terminal.ui.settings.UserSettingsProvider#getLinkActivationModifiersEx()}.
   */
  public @Nullable Integer getActivationModifiersEx() {
    return myActivationModifiersEx;
  }

  public interface PopupMenuGroupProvider {
    @NotNull List<TerminalAction> getPopupMenuGroup(@NotNull MouseEvent event);
  }

  public interface HoverConsumer {
    /**
     * Gets called when the mouse cursor enters the link's bounds.
     *
     * @param hostComponent terminal/console component containing the link
     * @param linkBounds    link's bounds relative to {@code hostComponent}
     */
    void onMouseEntered(@NotNull JComponent hostComponent, @NotNull Rectangle linkBounds);

    /**
     * Gets called when the mouse cursor exits the link's bounds.
     */
    void onMouseExited();
  }

  public static final class Builder {
    private Runnable myNavigateCallback;
    private PopupMenuGroupProvider myPopupMenuGroupProvider;
    private HoverConsumer myHoverConsumer;
    private Integer myActivationModifiersEx;
    private boolean myPreserveTextStyle;

    public @NotNull Builder setNavigateCallback(@NotNull Runnable navigateCallback) {
      myNavigateCallback = navigateCallback;
      return this;
    }

    public @NotNull Builder setPopupMenuGroupProvider(@Nullable PopupMenuGroupProvider popupMenuGroupProvider) {
      myPopupMenuGroupProvider = popupMenuGroupProvider;
      return this;
    }

    public @NotNull Builder setHoverConsumer(@Nullable HoverConsumer hoverConsumer) {
      myHoverConsumer = hoverConsumer;
      return this;
    }

    public @NotNull Builder setActivationModifiersEx(@Nullable Integer activationModifiersEx) {
      myActivationModifiersEx = activationModifiersEx;
      return this;
    }

    public @NotNull Builder setPreserveTextStyle(boolean preserveTextStyle) {
      myPreserveTextStyle = preserveTextStyle;
      return this;
    }

    public @NotNull LinkInfo build() {
      return new LinkInfoEx(myNavigateCallback, myPopupMenuGroupProvider, myHoverConsumer, myActivationModifiersEx,
                            myPreserveTextStyle);
    }
  }

  public static @Nullable PopupMenuGroupProvider getPopupMenuGroupProvider(@Nullable LinkInfo linkInfo) {
    return linkInfo instanceof LinkInfoEx ? ((LinkInfoEx) linkInfo).getPopupMenuGroupProvider() : null;
  }

  @Contract("null -> null")
  public static @Nullable HoverConsumer getHoverConsumer(@Nullable LinkInfo linkInfo) {
    return linkInfo instanceof LinkInfoEx ? ((LinkInfoEx) linkInfo).getHoverConsumer() : null;
  }

  @Contract("null -> null")
  public static @Nullable Integer getActivationModifiersEx(@Nullable LinkInfo linkInfo) {
    return linkInfo instanceof LinkInfoEx ? ((LinkInfoEx) linkInfo).getActivationModifiersEx() : null;
  }
}
