package software.netcore.radman.ui.menu;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import org.springframework.boot.info.BuildProperties;
import org.springframework.security.core.context.SecurityContextHolder;
import software.netcore.radman.ui.component.wizard.Wizard;
import software.netcore.radman.ui.component.wizard.demo.DemoDataStorage;
import software.netcore.radman.ui.component.wizard.demo.IntroductionStep;
import software.netcore.radman.ui.component.wizard.demo.Step2;
import software.netcore.radman.ui.component.wizard.demo.Step3;
import software.netcore.radman.ui.view.*;

import java.util.Objects;

/**
 * @since v. 1.0.0
 */
public class MenuTemplate extends Div implements RouterLayout {

    private static final long serialVersionUID = 2660673607800096107L;
    private static final String SELECTED_CLASS_NAME = "selected";

    private final UnorderedList linksContainer;

    public MenuTemplate(BuildProperties buildProperties) {
        addClassName("main-layout");
        setSizeFull();
        getStyle()
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("overflow", "hidden");

        Nav pageNav = new Nav();
        pageNav.setId("page-nav");
        pageNav.getStyle()
            .set("width", "100%")
            .set("z-index", "100")
            .set("color", "#e6e6e6")
            .set("background", "#24292e");

        HorizontalLayout accountNav = new HorizontalLayout();
        accountNav.addClassName("account-nav");
        accountNav.getStyle()
            .set("float", "right")
            .set("line-height", "3em");

        Div versionDiv = new Div();
        versionDiv.getStyle().set("padding", "0 10px");
        Span versionLabel = new Span(buildProperties.getVersion());
        versionDiv.add(versionLabel);

        Div logoutDiv = new Div();
        logoutDiv.getStyle().set("padding", "0 10px");
        Button logoutButton = new Button("Logout", VaadinIcon.SIGN_OUT.create());
        logoutButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        logoutButton.addClassName("logout");
        logoutButton.getStyle()
            .set("margin-top", "0")
            .set("color", "#e6e6e6");
        logoutButton.addClickListener(e -> logout());
        logoutDiv.add(logoutButton);

        accountNav.add(versionDiv, logoutDiv);

        linksContainer = new UnorderedList();
        linksContainer.setId("page-nav-links");
        linksContainer.getStyle()
            .set("margin", "0")
            .set("padding", "0")
            .set("line-height", "3em")
            .set("list-style-type", "none")
            .set("background-color", "#24292e");

        pageNav.add(accountNav, linksContainer);

        addCategoryName("RadMan");
        addNavigation(UsersView.class, "Users");
        addNavigation(UserGroupsView.class, "User groups");
        addNavigation(AttributesView.class, "Attributes");
        addSeparator();
        addCategoryName("Radius");
        addNavigation(NasView.class, "NAS");
        addNavigation(NasGroupsView.class, "NAS groups");
        addNavigation(AuthView.class, "Auth (AA)");
        addNavigation(AccountingView.class, "Accounting");
        addNavigation(UserToGroupView.class, "User/Group");
        addSeparator();
        addCategoryName("System");
        addNavigation(SystemUsersView.class, "System users");

        Div pageViewContainer = new Div();
        pageViewContainer.addClassName("page-view-container");
        pageViewContainer.getStyle()
            .set("flex", "1")
            .set("width", "100%")
            .set("display", "flex")
            .set("overflow-y", "auto");

        Div contentWrapper = new Div();
        contentWrapper.getStyle()
            .set("flex", "1")
            .set("margin", "0 auto")
            .set("max-width", "1600px");

        pageViewContainer.add(contentWrapper);

        add(pageNav, pageViewContainer);
        
        // Add styles
        addMenuStyles();
    }

    private void addMenuStyles() {
        getElement().executeJs(
            "const style = document.createElement('style');" +
            "style.textContent = `" +
            "#page-nav ul li { display: inline-block; vertical-align: middle; box-sizing: border-box; }" +
            "#page-nav ul li .separator { width: 10px; border-right: 1px solid #616161; height: 2.3em; }" +
            "#page-nav ul li .category { color: #9c9c9c; font-size: 13pt; font-weight: bold; padding: 0 10px; }" +
            "#page-nav ul li a { color: inherit; display: block; font-size: 13pt; font-weight: 500; " +
            "line-height: 3em; text-decoration: none; padding: 0 10px; transition: all 0.2s ease; }" +
            "#page-nav ul li a.selected, #page-nav ul li a:hover { background-color: #333e48; }" +
            "`;" +
            "document.head.appendChild(style);"
        );
    }

    private void addNavigation(Class<? extends Component> navigationTarget, String name) {
        ListItem li = new ListItem();
        RouterLink routerLink = new RouterLink(name, navigationTarget);
        routerLink.addClassName("button");
        li.add(routerLink);
        linksContainer.add(li);
        
        routerLink.setHighlightCondition((r, event) -> Objects.equals(r.getHref(), event.getLocation().getPath()));
        routerLink.setHighlightAction((r, highlight) -> {
            if (highlight) {
                routerLink.addClassName(SELECTED_CLASS_NAME);
            } else {
                routerLink.removeClassName(SELECTED_CLASS_NAME);
            }
        });
    }

    private void addCategoryName(String name) {
        ListItem li = new ListItem();
        Span span = new Span(name);
        span.addClassName("category");
        li.add(span);
        linksContainer.add(li);
    }

    private void addSeparator() {
        ListItem li = new ListItem();
        Div div = new Div();
        div.addClassName("separator");
        li.add(div);
        linksContainer.add(li);
    }

    private void logout() {
        SecurityContextHolder.clearContext();
        UI ui = UI.getCurrent();
        ui.getSession().getSession().invalidate();
        ui.getSession().close();
        ui.getPage().reload();
    }

    @SuppressWarnings("unused")
    private void add() {
        Wizard<DemoDataStorage> additionWizard = new Wizard<>(
                Wizard.Configuration.<DemoDataStorage>builder()
                        .title("Addition wizard")
                        .maxWidth("500px")
                        .step(new IntroductionStep())
                        .step(new Step2())
                        .step(new Step3())
                        .build(),
                dataStorage -> {
                    //no-op for demo
                },
                new DemoDataStorage());
        additionWizard.open();
    }

}
