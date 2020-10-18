package hellocucumber;

import static org.junit.Assert.assertEquals;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

class ModificateurAdresse {
	private boolean active;
	private String canal;
	private boolean isEnregistreSurTousLesContrats;
	private boolean isMouvementCree;
	
	ModificateurAdresse(boolean active, String canal) {
		this.active = active;
		this.canal = canal;
	}
	
	void executeModification() throws Exception {	
		CloseableHttpClient client = HttpClients.createDefault();
	    HttpPost httpPost = new HttpPost("http://localhost:1080/adresse");
	 
	    List<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("active", Boolean.toString(active)));
	    params.add(new BasicNameValuePair("canal", canal));
	    httpPost.setEntity(new UrlEncodedFormEntity(params));
	 
	    CloseableHttpResponse response = client.execute(httpPost);
	    JSONObject jsonObject = (JSONObject)new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse(
	    	      new InputStreamReader( response.getEntity().getContent(), "UTF-8"));
	    client.close();
	   
	    isMouvementCree = Boolean.parseBoolean((String) jsonObject.get("mvt_cree"));
		isEnregistreSurTousLesContrats =  Boolean.parseBoolean((String) jsonObject.get("contrat_update"));
	}

	public boolean isEnregistreSurTousLesContrats() {
		return isEnregistreSurTousLesContrats;
	}

	public boolean isMouvementCree() {
		return isMouvementCree;
	}   
}

public class StepDefinitions {
	private ModificateurAdresse modificateurAdresse;
    private boolean active;

    @Given("un abonné avec une adresse principale {string} en France")
    public void adresse_is(String active) {
    	this.active = "active".equals(active);
    }

    @When("le conseiller connecté à {string} modifie l'adresse de l'abonné")
    public void conseillerModifieAdresseDepuis(String canal) throws Exception {
    	modificateurAdresse = new ModificateurAdresse(active, canal);
    	modificateurAdresse.executeModification();
    }

    @Then("l'adresse de l'abonné modifiée est enregistrée sur l'ensemble des contrats de l'abonné")
    public void adresseEnregistreSurTousLesContrats() {
        assertEquals(active, modificateurAdresse.isEnregistreSurTousLesContrats());
    }
    
    @Then("un mouvement de modification d'adresse est créé")
    public void unMouvementEstCree() {
        assertEquals(true, modificateurAdresse.isMouvementCree());
    }
}
