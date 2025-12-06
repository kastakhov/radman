package software.netcore.radman.ui;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;

/**
 * Application shell configuration for Vaadin 23+
 * @since v. 1.0.0
 */
@Push
@BodySize(height = "100vh", width = "100vw")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
public class AppShell implements AppShellConfigurator {
    private static final long serialVersionUID = 1L;
}
