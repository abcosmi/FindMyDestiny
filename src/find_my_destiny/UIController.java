package find_my_destiny;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.sql.ResultSet;

import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Model;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.inject.Inject;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;

import org.primefaces.component.commandbutton.CommandButton;

@RequestScoped
@ManagedBean
public final class UIController {
	private Connection Conn;
    
	@Inject
        private User user;
	@Inject
        private Package tourPackage;
	@Inject
        private Login login;
    @Inject
        private SessionController session;
    @Inject
        private GoogleNearbyApi googleNearbyApi;
	
	// TODO: this is temporary
	private String packagesFromUserHtml;
	
	private static final String Database_ServerName = "jdbc:mysql://localhost:3306/find_my_destiny";
	private static final String Database_User = "root";
	private static final String Database_Password = "root";
	
	public String searchPackages()
	{
        return "";
    }
    
    public void deletePackage(String id)
    {
        UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
        UIComponent component = (UIComponent)viewRoot.findComponent("form_"+id);
        component.getParent().setRendered(false);
        component.getParent().clearInitialState();
    }
    
    public void destinationSearchBuildUI()
    {
        UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
        HtmlPanelGroup component = (HtmlPanelGroup)viewRoot.findComponent("places_hub");
        component.setLayout("block");
        
        int resultsCount = googleNearbyApi.getResultsCount();
        
        for (int i = 0; i < resultsCount; i++)
        {
            if (component.findComponent("form_"+i) == null)
            {
                String place = googleNearbyApi.getPlace(i);
                float latitude = googleNearbyApi.getLatitude(i);
                float longitude = googleNearbyApi.getLongitude(i);
                
                HtmlPanelGroup panelGroup = new HtmlPanelGroup();
                panelGroup.setLayout("block");
                panelGroup.setStyleClass("add_to_list");
                
                HtmlForm form = new HtmlForm();
                form.setId("form_"+i);
                
                HtmlOutputText outputText = new HtmlOutputText();
                outputText.setValue(place);
                outputText.setEscape(false);
                
                HtmlPanelGroup buttonGroup = new HtmlPanelGroup();
                buttonGroup.setLayout("block");
                
                MethodBinding methodBinding = FacesContext.getCurrentInstance().getApplication().createMethodBinding("#{connectionBean.insertDestination('"+place+"', "+latitude+", "+longitude+")}", 
                                                                                                                     new Class[] {int.class});
                
                CommandButton appButton = new CommandButton();
                appButton.setStyleClass("btn button_delete");
                appButton.setValue("adicionar");
                appButton.setType("submit");
                appButton.setIcon("fa fa-plus");
                appButton.setStyle("padding:5px 10px");
                appButton.setActionListener(methodBinding);
                appButton.setUpdate("places_hub");
                
                CommandButton showButton = new CommandButton();
                showButton.setStyleClass("btn button_delete");
                showButton.setValue("Mostrar no mapa");
                showButton.setType("submit");
                showButton.setIcon("fa fa-plus");
                showButton.setStyle("padding:5px 10px");
                showButton.setActionListener(methodBinding);
                showButton.setUpdate("places_hub");
                
                component.getChildren().add(panelGroup);
                panelGroup.getChildren().add(form);
                form.getChildren().add(outputText);
                form.getChildren().add(buttonGroup);
                buttonGroup.getChildren().add(appButton);
                buttonGroup.getChildren().add(showButton);
            }
        }
    }
}