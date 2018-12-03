package find_my_destiny;

import find_my_destiny.GooglePlacesSearchApi;
import find_my_destiny.GoogleNearbyApi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

@SuppressWarnings({"deprecation", "unused"})
@ManagedBean
@SessionScoped
public class FindMyDestinySearch 
{
	private String destinationQuery = null;
	private String placeSearchAPIUrl = "https://maps.googleapis.com/maps/api/place/findplacefromtext/";
	private String placeDetailsAPIUrl = "https://maps.googleapis.com/maps/api/place/details/";
    private String nearbyApiUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/";
	private String retrivalFormat = "json";
	private String myGoogleAPIKey = "AIzaSyDvbyVqbMLuaj-GB2GUfQKdU1SapIp6Dao";
	private String placeId = null;
	private float latitude  = 0.0f;
	private float longitude = 0.0f;
	private String placeName = null;
    
	@Inject
        private GooglePlacesSearchApi apiWraper;
    @Inject
        private GoogleNearbyApi nearbyApi;
    @Inject
        private ConnectionBean connectionBean;
    
    private int resultsCount = 0;
    
    private String htmlListOfPlaces = null;
	
    public String getDestinationQuery()
	{
		return destinationQuery;
	}
	public void setDestinationQuery(String destinationQuery)
	{
		this.destinationQuery = destinationQuery;
	}
	
	public float getLatitude()
	{
        if (apiWraper != null)
        {
            latitude = apiWraper.getLatitude();
        }
		
        return latitude;
	}
	
	public float getLongitude()
	{
        if (apiWraper != null)
        {
            longitude = apiWraper.getLongitude();
        }
        
        return longitude;
	}
    
    public String getPlaceName()
    {
        placeName = apiWraper.getFormattedAddress();
        return placeName;
    }
	
	// Search for destination. Do not return anything in case it can't find destination
	public void searchDestination()
	{	
		try
		{
			destinationQuery = URLEncoder.encode(destinationQuery,"UTF-8");
			JSONObject json = fetchJSONDataFromAPI(placeSearchAPIUrl + retrivalFormat+ "?key=" + myGoogleAPIKey + "&input=" + destinationQuery + "&inputtype=textquery");
			System.out.println("query: "+ destinationQuery);
			
			if (json != null && json.getString("status").equals("OK"))
			{
				JSONArray destinationsArray = (JSONArray) json.get("candidates");
				JSONObject destinationsObject = (JSONObject)destinationsArray.get(0);
				placeId = destinationsObject.getString("place_id");
				System.out.println("Destiny Id: "+placeId);
				
				json = fetchJSONDataFromAPI(placeDetailsAPIUrl + retrivalFormat+ "?placeid=" + placeId + "&key="+myGoogleAPIKey);
                
                apiWraper.buildApi(json);
                System.out.println(apiWraper.toString());
                
                String urlBuild = nearbyApiUrl+retrivalFormat+
                    "?key="+myGoogleAPIKey+
                    "&location="+apiWraper.getLatitude()+","+apiWraper.getLongitude()+
                    "&radius=10000"+
                    "&rankedby=prominence"+
                    "&type=park|museum|movie_theater|casino|city_hall|shopping_mall|night_club|stadium|amusement_park|aquarium";
                json = fetchJSONDataFromAPI(urlBuild);
                nearbyApi.buildApi(json);
                System.out.println(urlBuild);
                
                System.out.println(nearbyApi.getResultsCount());
                
                connectionBean.setLat(latitude);
                connectionBean.setLat(longitude);
                
                HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
                HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
                ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
                externalContext.redirect("destination_search.xhtml");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
    public String refreshMap()
    {
        System.out.println("refreshing map");
        
        return "OK";
    }
    
	private JSONObject fetchJSONDataFromAPI(String UrlToFetchFrom)
	{
		StringBuilder stringBuilder = new StringBuilder();
		
		try
		{
			URL url = new URL(UrlToFetchFrom);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
			for (String line; (line = reader.readLine()) != null;) 
			{
				stringBuilder.append(line);
			}
		}
		
		catch(Exception e)
		{
			return null;
		}
		
		return new JSONObject(stringBuilder.toString()); 
	}
    
    private GooglePlacesSearchApi getApiWraper()
    {
        return apiWraper;
    }
    
    public String getHtmlListOfPlaces()
    {
        return nearbyApi.getHtmlListOfPlaces();
    }
    
    public int getResultsCount()
    {
        return nearbyApi.getResultsCount();
    }
}
