package find_my_destiny;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.bean.ManagedBean;
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

@SuppressWarnings("deprecation")
@ManagedBean
@SessionScoped
public final class ConnectionBean {
	private Connection Conn;
    
	@Inject
        private User user;
	@Inject
        private Package tourPackage;
	@Inject
        private Login login;
    @Inject
        private SessionController session;
	
	// TODO: this is temporary
	private String packagesFromUserHtml;
	
	private static final String Database_ServerName = "jdbc:mysql://localhost:3306/find_my_destiny";
	private static final String Database_User = "root";
	private static final String Database_Password = "root";
	
	public boolean open()
	{
		try
		{	
			Class.forName("com.mysql.cj.jdbc.Driver");
			Conn = DriverManager.getConnection(Database_ServerName+
                                               "?user="+Database_User+"&password="+Database_Password+"&useTimezone=true&serverTimezone=UTC");
		}
		catch(Exception e)
		{
			System.out.println("Error: connection not opened");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean close()
	{
		try
		{
			Conn.close();
		}
		catch(Exception e)
		{
			return false; 
		}
		System.out.println("Connection closed successfully");
		return true;
	}
	
	public void insertUserIntoDatabase()
	{
		try
		{
			java.sql.Connection Conn = this.getConnection();
			Statement statement = Conn.createStatement();
            
			String name = user.getName();
			String username = user.getUsername();
			String cpf = user.getCpf();
			String address = user.getAddress();
			String password = user.getPassword();
			String telephone = user.getTelephone();
			String email = user.getEmail();
			statement.executeUpdate("INSERT INTO user (fullName, email, username, cpf, address, telephone, password) VALUES('"+name+"','"+email+"','"+username+"', '"+cpf+"','"+address+"','"+telephone+"','"+password+"');");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
    public void commitUserData(String name, String username, String cpf, String email, String address, String telephone,
                               String cellphone, String password)
    {
        session.getLoggedUser().setName(name);
        session.getLoggedUser().setUsername(username);
        session.getLoggedUser().setCpf(cpf);
        session.getLoggedUser().setAddress(address);
        session.getLoggedUser().setTelephone(telephone);
        session.getLoggedUser().setCellphone(cellphone);
        session.getLoggedUser().setPassword(password);
        session.getLoggedUser().setEmail(email);
    }
    
    public void loginUser()
    {
        try
        {
            java.sql.Connection Conn = this.getConnection();
			Statement statement = Conn.createStatement();
            
            String username = session.getLoggedUser().getUsername();
            String password = session.getLoggedUser().getPassword();
            
            String sql = "SELECT username, id from user where username like '"+
                username+"' and password like '"+password+"'";
            
            ResultSet resultSet = statement.executeQuery(sql);
            
            while(resultSet.next())
            {
            	login.setLoginStatus(true);
            	session.getLoggedUser().setId(resultSet.getInt("id"));
            	session.getLoggedUser().setUsername(resultSet.getString("username"));
            	ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            	externalContext.redirect("hub.xhtml");
            }
        }
        
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
	public void PrintConnection()
	{
		System.out.println("Connection info:");
	}
	
	public void signOut()
	{
		// TODO: sign out code, like clear logged flag and session indicators of login
		
		try
		{
			login.setLoginStatus(false);
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			externalContext.redirect("home.xhtml");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void createPackage()
	{
		this.open();
		try
		{
			java.sql.Connection Conn = this.getConnection();
			Statement statement = Conn.createStatement();
			
			String packageName = tourPackage.getName();
			String SQL = "INSERT INTO tourism_package (package_name, user_id) VALUES('"+packageName+"', "+user.getId()+")";
			statement.executeUpdate(SQL);
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		this.close();
	}
	
	public String searchPackages()
	{
		this.open();
		try
		{
			java.sql.Connection Conn = this.getConnection();
			Statement statement = Conn.createStatement();
			String sql = "SELECT id, package_name from tourism_package where user_id = "+login.getUser().getId();
			ResultSet resultSet = statement.executeQuery(sql);
            
            UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
            HtmlPanelGroup component = (HtmlPanelGroup)viewRoot.findComponent("tours");
            component.setLayout("block");
            
            System.out.println(component.isRendered());
            
            packagesFromUserHtml = "<ul>"; 
            
            while(resultSet.next())
            {
                // NOTE: Right now we are passing the id of package as the id of form
                String packageName = resultSet.getString("package_name");
                int id = resultSet.getInt("id");
                
                if (component.findComponent("form_"+id) == null)
                {
                    HtmlPanelGroup panelGroup = new HtmlPanelGroup();
                    panelGroup.setLayout("block");
                    panelGroup.setStyleClass("add_to_list");
                    
                    HtmlForm form = new HtmlForm();
                    form.setId("form_"+id);
                    
                    HtmlOutputText outputText = new HtmlOutputText();
                    outputText.setValue(packageName);
                    outputText.setEscape(false);
                    
                    HtmlPanelGroup buttonGroup = new HtmlPanelGroup();
                    buttonGroup.setLayout("block");
                    
                    MethodBinding methodBinding = FacesContext.getCurrentInstance().getApplication().createMethodBinding("#{connectionBean.deletePackage("+id+")}", 
                                                                                                                         new Class[] {int.class});
                    
                    /*
                    ExpressionFactory expressionFactory = ExpressionFactory.newInstance();
                    MethodExpression methodExpression = expressionFactory.createMethodExpression(FacesContext.getCurrentInstance().getELContext(), 
                                                                                                 "#{connectionBean.deletePackage("+id+")}",
                                                                                                 String.class,
                                                                                                 new Class<?>[] { int.class });
                    */
                    
                    CommandButton deleteButton = new CommandButton();
                    deleteButton.setStyleClass("btn button_delete");
                    deleteButton.setValue("deletar");
                    deleteButton.setType("submit");
                    deleteButton.setIcon("fa fa-minus");
                    deleteButton.setStyle("padding:5px 10px");
                    deleteButton.setActionListener(methodBinding);
                    deleteButton.setUpdate("tours");
                    
                    CommandButton expandButton = new CommandButton();
                    expandButton.setStyleClass("btn button_expand");
                    expandButton.setValue("Detalhes");
                    expandButton.setType("submit");
                    expandButton.setIcon("fa fa-expand");
                    expandButton.setStyle("padding:5px 10px");
                    
                    CommandButton sendButton = new CommandButton();
                    sendButton.setStyleClass("btn button_send");
                    sendButton.setValue("Enviar");
                    sendButton.setType("submit");
                    sendButton.setIcon("fa fa-send");
                    sendButton.setStyle("padding:5px 10px");
                    
                    component.getChildren().add(panelGroup);
                    panelGroup.getChildren().add(form);
                    form.getChildren().add(outputText);
                    form.getChildren().add(buttonGroup);
                    buttonGroup.getChildren().add(deleteButton);
                    buttonGroup.getChildren().add(expandButton);
                    buttonGroup.getChildren().add(sendButton);
                }
            }
            packagesFromUserHtml += "</ul>";
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
        this.close();
        
        
        FacesContext context = FacesContext.getCurrentInstance();
        
        return context.getApplication().evaluateExpressionGet(context, packagesFromUserHtml, String.class);
    }
    
    public String getPackagesFromUserHtml() {return searchPackages();}
    
    public void deletePackage(int id)
    {
        this.open();
        try
        {
            java.sql.Connection Conn = this.getConnection();
			Statement statement = Conn.createStatement();
			String sql = "DELETE FROM tourism_package where id = "+ id;
			statement.executeUpdate(sql);
            
            UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
            UIComponent component = (UIComponent)viewRoot.findComponent("form_"+id);
            //component.setParent(null);
            component.getParent().setRendered(false);
            component.getParent().clearInitialState();
            
            // NOTE: Update after deletion
            System.out.println("update");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        this.close();
        
        searchPackages();
    }
    
    public Connection getConnection() {return Conn;}
    
    public SessionController getSession() {return session;}
}