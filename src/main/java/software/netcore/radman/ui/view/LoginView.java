package software.netcore.radman.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import software.netcore.radman.buisness.service.security.SecurityService;

@PageTitle("Login")
@Route(value = "login")
public class LoginView extends Div implements BeforeEnterObserver {

    private static final long serialVersionUID = 8317537279357271016L;

    private final SecurityService securityService;
    private final Span authenticationMessage = new Span("Incorrect username or password");

    public LoginView(SecurityService securityService) {
        this.securityService = securityService;
        
        addClassName("login-view");
        setSizeFull();
        
        VerticalLayout container = new VerticalLayout();
        container.setSizeFull();
        container.setJustifyContentMode(VerticalLayout.JustifyContentMode.CENTER);
        container.setAlignItems(VerticalLayout.Alignment.CENTER);
        
        FormLayout loginForm = new FormLayout();
        loginForm.setMaxWidth("400px");
        
        H1 title = new H1("RadMan");
        
        authenticationMessage.addClassName("error-message");
        authenticationMessage.getStyle()
            .set("padding", "10px 20px")
            .set("color", "#ffffff")
            .set("font-weight", "bold")
            .set("border-radius", "2px")
            .set("background-color", "#ff5353")
            .set("margin-bottom", "10px")
            .set("display", "none");
        
        TextField username = new TextField();
        username.setWidthFull();
        username.setAutofocus(true);
        username.addValueChangeListener(e -> authenticationMessage.getStyle().set("display", "none"));
        
        PasswordField password = new PasswordField();
        password.setWidthFull();
        password.addValueChangeListener(e -> authenticationMessage.getStyle().set("display", "none"));
        
        Button loginButton = new Button("Login");
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.setWidthFull();
        
        // Create native form for Spring Security
        getElement().executeJs(
            "const form = document.createElement('form');" +
            "form.method = 'post';" +
            "form.action = 'login';" +
            "const usernameInput = document.createElement('input');" +
            "usernameInput.type = 'hidden';" +
            "usernameInput.name = 'username';" +
            "usernameInput.id = 'username-field';" +
            "const passwordInput = document.createElement('input');" +
            "passwordInput.type = 'hidden';" +
            "passwordInput.name = 'password';" +
            "passwordInput.id = 'password-field';" +
            "form.appendChild(usernameInput);" +
            "form.appendChild(passwordInput);" +
            "this.appendChild(form);"
        );
        
        loginButton.addClickListener(e -> {
            getElement().executeJs(
                "document.getElementById('username-field').value = $0;" +
                "document.getElementById('password-field').value = $1;" +
                "this.querySelector('form').submit();",
                username.getValue(),
                password.getValue()
            );
        });
        
        password.addKeyPressListener(e -> {
            if (e.getKey().getKeys().contains("Enter")) {
                loginButton.click();
            }
        });
        
        VerticalLayout formContent = new VerticalLayout(title, authenticationMessage, username, password, loginButton);
        formContent.setSpacing(true);
        formContent.setPadding(true);
        formContent.setMaxWidth("400px");
        
        container.add(formContent);
        add(container);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // If auto-login is enabled, redirect to home page
        if (securityService.isAutoLoginEnabled()) {
            event.forwardTo(UsersView.class);
            return;
        }
        
        securityService.initiateFallbackUser();
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            authenticationMessage.getStyle().set("display", "block");
        }
    }
}
