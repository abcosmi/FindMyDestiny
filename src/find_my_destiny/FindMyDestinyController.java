package find_my_destiny;

import java.util.Date;
import javax.enterprise.inject.Model;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.Pattern;

@Model
@Named
@ViewScoped
public class FindMyDestinyController {
    private String name;
    private String username;
    @Pattern(regexp="[0-9]{3}.[0-9]{3}.[0-9]{3}-[0-9]{2}", message="Formato de cpf não reconhecido")
        private String cpf;
    private String address;
    private String telephone;
    private String cellphone;
    private String email;
    private String password;
    
    public String getName(){return name;}
    public void setName(String name){this.name = name;}
    public String getUsername(){return username;}
    public void setUsername(String username){this.username = username;}
    public String getCpf(){return cpf;}
    public void setCpf(String cpf){this.cpf = cpf;}
    public String getAddress(){return address;}
    public void setAddress(String address){this.address = address;}
    public String getTelephone(){return telephone;}
    public void setTelephone(String telephone){this.telephone = telephone;}
    public String getPassword(){return password;}
    public void setPassword(String password){this.password = password;}
    public String getEmail(){return email;}
    public void setEmail(String email){this.email = email;}
    public String getCellphone() {return cellphone;}
    public void setCellphone(String cellphone) {this.cellphone = cellphone;}
    
    @Inject
        private ConnectionBean Connection;
    @Inject
        private User user;
    @Inject
        private Package tourPackage;
    @Inject
        private Login login;
    @Inject
        private FindMyDestinySearch search;
    @Inject
        private UIController uiController;
    
    //public User getUser() {return user;}
    
    public ConnectionBean CreateConnection()
	{
		return Connection;
	}
	
	public void login()
	{
		Connection.open();	
		// TODO: login code here (maybe JAAS?)
        Connection.loginUser();
        clearUserData();
		Connection.close();
	}
	
	public void createUser()
	{
		Connection.open();
        Connection.commitUserData(name, username, cpf, email, address, 
                                  telephone, cellphone, password);
		Connection.insertUserIntoDatabase();
        Connection.close();
        
        setName("");
        setUsername("");
        setCpf("");
        setAddress("");
        setTelephone("");
        setPassword("");
        setEmail("");
        setCellphone("");
        
        login();
        
        try
        {
            ExternalContext externalContext = 
                FacesContext.getCurrentInstance().getExternalContext();
            externalContext.redirect("hub.xhtml");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
	}
	
	public void updateUser()
	{
		Connection.open();
		
		// TODO: user update information code here!
		
		Connection.close();
	}
	
	public void readUser()
	{
		Connection.open();
		
		// TODO: user read information code here!
		
		Connection.close();
	}
	
	public void deleteUser()
	{
		Connection.open();
		
		// TODO: user deletion code here!
		
		Connection.close();
	}
	
	public void signOut()
	{
		Connection.signOut();
	}
	
	public void newPackage()
	{
		Connection.createPackage();
		updatePackagesList();
	}
	
	public void updatePackagesList()
	{
		Connection.searchPackages();
	}
	
	public void init(String page)
	{
		String viewId = FacesContext.getCurrentInstance().getExternalContext().getRequestServletPath(); 
		if(!login.getLoginStatus() && viewId.equals("/hub.xhtml"))
		{
			try
			{
				ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
				externalContext.redirect("home.xhtml");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		if (viewId.equals("/hub.xhtml"))
		{
			updatePackagesList();
		}
        
        if (viewId.equals("/home.xhtml"))
        {
            Connection.setLat(0.0f);
            Connection.setLng(0.0f);
        }
        
        if (viewId.equals("/destination_search.xhtml"))
        {
            Connection.setLat(0.0f);
            Connection.setLng(0.0f);
            uiController.destinationSearchBuildUI();
        }
	}
	
	public Package getTourPackage() {return tourPackage;}
	public void setTourPackage(Package tourPackage) {this.tourPackage= tourPackage;}
	
	public Date getTime() {return new Date();}
    
    public void clearUserData()
    {
        name = "";
        username = "";
        cpf = "";
        email = "";
        address= "";
        telephone= "";
        cellphone = "";
        password = "";
    }
}
