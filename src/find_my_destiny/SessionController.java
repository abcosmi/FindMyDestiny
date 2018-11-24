package find_my_destiny;

import javax.enterprise.inject.Model;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.inject.Singleton;

@ManagedBean
@SessionScoped
@Singleton
public class SessionController {
	private boolean logged;
	
	@Inject
        private User loggedUser;
	
    public User getLoggedUser()
	{
		return loggedUser;
	}
}
