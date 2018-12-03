package find_my_destiny;

import find_my_destiny.FindMyDestinyI18n;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.inject.Named;
import javax.inject.Singleton;

import java.util.List;
import java.util.ArrayList;

import org.json.JSONObject;
import org.primefaces.component.commandbutton.CommandButton;
import org.json.JSONArray;

@SuppressWarnings("deprecation")
@ManagedBean
@SessionScoped
@Named
@Singleton
public class GoogleNearbyApi
{
    private String nextPageToken;
    private JSONArray results;
    
    private List<String> places;
    private List<Float> latitude;
    private List<Float> longitude;
    private int resultsCount = 0;
    
    private String htmlListOfPlaces = null;
    private int placeIndex = 0;
    
    public void buildApi(JSONObject response)
    {
        if (response.getString("status").equals("OK"))
        {
            places = new ArrayList<String>();
            latitude = new ArrayList<Float>();
            longitude= new ArrayList<Float>();
            
            results = response.getJSONArray("results");
            //nextPageToken = response.getString("next_page_token");
            resultsCount = results.length();
            
            for(int i = 0; i < results.length(); i++)
            {
                //GooglePlaceDefinition place = new GooglePlaceDefinition(results.getJSONObject(i));
                JSONObject place = results.getJSONObject(i);
                places.add(place.getString("name"));
                JSONObject geometry = place.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");
                System.out.println(location.getFloat("lat"));
                latitude.add(location.getFloat("lat"));
                longitude.add(location.getFloat("lng"));
                System.out.println(places.get(i));
            }
            
            htmlListOfPlaces = "";
            FindMyDestinyI18n i18n = new FindMyDestinyI18n();
        }
    }
    
    public String getHtmlListOfPlaces()
    {
        return htmlListOfPlaces;
    }
    
    public void setPlaceIndex(int placeIndex)
    {
        this.placeIndex = placeIndex;
    }
    
    public int getResultsCount()
    {
        return resultsCount;
    }
    
    public GoogleNearbyApi getApi()
    {
    	return new GoogleNearbyApi();
    }
    
    public String getPlace(int placeIndex)
    {
        return places.get(placeIndex);
    }
    
    public Float getLatitude(int placeIndex)
    {
        return latitude.get(placeIndex);
    }
    
    public Float getLongitude(int placeIndex)
    {
        return longitude.get(placeIndex);
    }
}
