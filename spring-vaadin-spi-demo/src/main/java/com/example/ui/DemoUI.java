package com.example.ui;

import java.util.Locale;
import java.util.ServiceLoader;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventBusListener;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.i18n.I18N;

import com.example.sharedapi.GraphicTool;
import com.example.sharedapi.GraphicToolsSet;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

//@EnableVaadinExtensions
@SpringUI
@Theme("valo")
public class DemoUI extends UI implements EventBusListener<Object> {

	@Value("${enable.second.button}")
	private Boolean enableSecondButton;
	
	@Autowired
    I18N i18n;
	
	@Autowired
    EventBus.UIEventBus eventBus;
	
	private static ServiceLoader<GraphicToolsSet> graphicToolsSetLoader = ServiceLoader.load(GraphicToolsSet.class);
	
	@Override
	protected void init(VaadinRequest request) {
		eventBus.subscribe(this);
		
		final VerticalLayout layout = new VerticalLayout();
        final TextField name = new TextField();
        name.setCaption(i18n.get("demo.name.prompt"));

        Button button = new Button(i18n.get("demo.button.caption"));
        button.addClickListener(e -> {
        	Notification.show(i18n.get("demo.notification"));
        });
        
        Button button2 = new Button("Set locale to DE");
        button2.addClickListener(e -> {
        	setLocale(Locale.GERMANY);
        	eventBus.publish(EventScope.UI, DemoUI.this, "Locale changed to Germany");
        });
        
        button2.setEnabled(enableSecondButton);
        layout.addComponents(name, button, button2);
        setContent(layout);
        
        button2.addClickListener(e -> {
        	setLocale(Locale.GERMANY);
        	eventBus.publish(EventScope.UI, DemoUI.this, "Locale changed to Germany");
        });
        
        GraphicTool tool = getGraphicTool("org.vaadin.SketchCanvas");
        if (tool!=null) {
        	layout.addComponent((Component) tool);
        }
                
	}
	
	@Override
    public void onEvent(final org.vaadin.spring.events.Event<Object> event) {
        getUI().access(new Runnable() {
            @Override
            public void run() {
            	Notification.show(event.getPayload().toString());
            }
        });
    }

	public GraphicTool getGraphicTool(String toolName) {
		for (GraphicToolsSet toolSet : graphicToolsSetLoader) {
			GraphicTool tool = toolSet.getTool(toolName);
	         if (tool != null)
	             return tool;
	     }
	     return null;
	}
	
    @PreDestroy
    void destroy() {
        eventBus.unsubscribe(this);
    }
}
