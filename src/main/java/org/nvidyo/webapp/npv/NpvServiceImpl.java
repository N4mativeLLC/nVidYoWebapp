package org.nvidyo.webapp.npv;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nvidyo.webapp.database.ConnectionPool;
import org.nvidyo.webapp.npv.Campaign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analytics.Analytics.Data.Ga.Get;
import com.google.api.services.analytics.model.Accounts;
import com.google.api.services.analytics.model.GaData;
import com.google.api.services.analytics.model.Profiles;
import com.google.api.services.analytics.model.Webproperties;
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;
//import com.n4mative.csvParser.Client;
import com.opencsv.CSVWriter;

@Service("npvService")
//@Transactional
public class NpvServiceImpl implements NpvService {
	
	@Autowired
	private Environment env;
	//static String host = "192.168.1.71";
    //static String db = "NPV_QA";
    
	
	/**
	 * Demonstrates using the Text to Speech client to synthesize text or ssml.
	 *
	 * <p>Note: ssml must be well-formed according to: (https://www.w3.org/TR/speech-synthesis/
	 * Example: <speak>Hello there.</speak>
	 *
	 * @param ssml the ssml document to be synthesized. (e.g., "<?xml...")
	 */
	public byte[] synthesizeSsml(String ssml,String gender, String language) {
		ByteString audioContents = null;
		SsmlVoiceGender setgender = null;
	  // Instantiates a client
	  try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
	    // Set the ssml input to be synthesized
	    SynthesisInput input = SynthesisInput.newBuilder().setSsml(ssml).build();

	    if(gender.toLowerCase().equals("male")){
	    	setgender = SsmlVoiceGender.MALE;
	    }else{
	    	setgender = SsmlVoiceGender.FEMALE;
	    }
	    // Build the voice request
	    VoiceSelectionParams voice =
	        VoiceSelectionParams.newBuilder()
	            .setLanguageCode(language) // languageCode = "en_us"
	            .setSsmlGender(setgender) // ssmlVoiceGender = SsmlVoiceGender.FEMALE
	            .build();

	    // Select the type of audio file you want returned
	    AudioConfig audioConfig =
	        AudioConfig.newBuilder()
	            .setAudioEncoding(AudioEncoding.MP3) // MP3 audio.
	            .build();

	    // Perform the text-to-speech request
	    SynthesizeSpeechResponse response =
	        textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

	    // Get the audio contents from the response
	    audioContents = response.getAudioContent();

	    // Write the response to the output file.
	    /*try (OutputStream out = new FileOutputStream("output.mp3")) {
	      out.write(audioContents.toByteArray());
	      System.out.println("Audio content written to file \"output.mp3\"");
	    }*/
	  } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return audioContents.toByteArray();
	}

	@Override
	public String getAnalyticsData(String startdate, String endtdate) {
		
		final String APPLICATION_NAME = "GA Data Ingestion";
	    final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	    //String KEY_FILE_LOCATION = "/Users/surbhi/Documents/workspace/nVidYoWebapp/src/main/java/org/nvidyo/webapp/npv/videoJSTest.json";
	    String KEY_FILE_LOCATION = "/Users/rk/Documents/workspace/nVidYoWebapp/src/main/java/org/nvidyo/webapp/npv/videoJSTest.json";
	    String dimsMetric = "ga:pageviews";
        String dimsDimension = "ga:date, ga:pagePath,ga:pageTitle";
        String dimsMetric_event = "ga:totalEvents,ga:eventValue,ga:sessionsWithEvent";
        String dimsDimension_event ="ga:eventCategory,ga:eventAction,ga:eventLabel";
        String startDate = startdate;
        String endDate = endtdate;
        JSONArray jArray = new JSONArray();
        List<Campaign> campaignLst = new ArrayList<>();
        try {
        	
        	JSONObject jsonobj = new JSONObject();
        	//JSONObject jsonobj_event = new JSONObject();
        	
        	Analytics analytics = initializeAnalytic(KEY_FILE_LOCATION,JSON_FACTORY, APPLICATION_NAME );
            String profile = getFirstProfileId(analytics);
            System.out.println("First Profile Id is: " + profile);
            
            GaData result = getResults(analytics, profile, dimsMetric, dimsDimension, startDate, endDate);
            GaData result_event = getResults(analytics, profile, dimsMetric_event, dimsDimension_event, startDate, endDate);
            
            jsonobj.put("data", result);
            jsonobj.put("event", result_event);
        	
            jArray.put(jsonobj);
            //campaignLst= generateCampaignFolder(result);
            //insertAnalytics(campaignLst);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
        
	}
	
	private static GaData getResults(Analytics analytics, String profileId, 
            String dimsMetric, String dimsDimension, 
            String startDate, String endDate) throws IOException {
        // Query the Core Reporting API for the number of sessions
        // String dimsDate = ",ga:date, ga:hour";       
        
        Get get = analytics.data().ga().get("ga:" + profileId, startDate, endDate, dimsMetric);
        get.setDimensions(dimsDimension);
        get.setMaxResults(10000);
        
        return get.execute();
      }
	
	
	private static Analytics initializeAnalytic(String KEY_FILE_LOCATION, JsonFactory JSON_FACTORY, String APPLICATION_NAME) throws GeneralSecurityException, IOException {
        
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
       
         //Using JSON file for secret key
        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(KEY_FILE_LOCATION))
                                                        .createScoped(AnalyticsScopes.all());
        
        
        return new Analytics.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME).build();
    }
	
	private static String getFirstProfileId(Analytics analytics) throws IOException {
        // Get the first view (profile) ID for the authorized user.
        String profileId = null;

        // Query for the list of all accounts associated with the service account.
        Accounts accounts = analytics.management().accounts().list().execute();

        if (accounts.getItems().isEmpty()) {
          System.err.println("No accounts found");
        } else {
          String firstAccountId = accounts.getItems().get(0).getId();

          // Query for the list of properties associated with the first account.
          Webproperties properties = analytics.management().webproperties()
              .list(firstAccountId).execute();

          if (properties.getItems().isEmpty()) {
            System.err.println("No Webproperties found");
          } else {
            String firstWebpropertyId = properties.getItems().get(0).getId();

            // Query for the list views (profiles) associated with the property.
            Profiles profiles = analytics.management().profiles()
                .list(firstAccountId, firstWebpropertyId).execute();

            if (profiles.getItems().isEmpty()) {
              System.err.println("No views (profiles) found");
            } else {
              // Return the first (view) profile associated with the property.
              profileId = profiles.getItems().get(0).getId();
            }
          }
        }
        return profileId;
      }

		
	private  void insertAnalytics(List<Campaign> campaignLst) {
		System.out.println("host: " + env.getProperty("mysql.host") + "  db: " + env.getProperty("mysql.db"));
	    CallableStatement stmt = null;
		String strSQL = "{call sp_InsertAnalyticsData(?,?,?,?,?)}";
		
		
		try (Connection conn = ConnectionPool.getConnection(env.getProperty("mysql.host"), env.getProperty("mysql.db"),env.getProperty("mysql.username"))) {
			for (Campaign cmp : campaignLst) {	
				stmt = conn.prepareCall(strSQL);
				// set all the preparedstatement parameters
				stmt.setString(1, cmp.getDate());
				stmt.setString(2, cmp.getPagePath());
				stmt.setInt(3, cmp.getPageView());
				stmt.setString(4, cmp.getCampaignName());
				stmt.setString(5, cmp.getClientName());
				// execute the preparedstatement insert
				stmt.executeQuery();
			    //st.close();
				
			    //return true;
			}
			System.out.println("Record Inserted");
		  } 
		  catch (Exception e)
		  {
			  //throw new RuntimeException(e);
			  e.printStackTrace();
		  } finally {
	            try {
	            	if (stmt != null) {
	            		stmt.close();
	            	}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		//return ;
	}
	
	private static List<Campaign> generateCampaignFolder(GaData results) {
        
        // (3) Get data from GaData object
		List<List<String>> rows = results.getRows();
		int pageView=0;
		//String row="";
		//int uniquePage=0;
		//Map<String,Integer> map=new HashMap<String,Integer>();
		List<Campaign> campList= new ArrayList<Campaign>();
		List<String> record=null;
		
		for(int i=0;i<rows.size();i++){
			
			Campaign camp = new Campaign();
		    String url= "";
		    record=rows.get(i);
			url = record.get(1).toString();
			//String folder = url.split("\\?")[1].substring(18,27);
			String folder = url.split("\\?")[1].split("/")[3];
			String clientName=url.split("\\?")[1].split("/")[2];
			pageView = Integer.parseInt(record.get(3));
			camp.setCampaignName(folder);
        	camp.setDate(record.get(0));
    		//System.out.println(folder);
    		//System.out.println(clientName);
    		
    		camp.setPageView(pageView);
    		camp.setPagePath(url);
    		camp.setClientName(clientName);
    		campList.add(camp);

		}
		
		return campList;
    }



	@Override
	public String getCount(String campaign, String category,String startdate, String endtdate) {
	
		PreparedStatement ps = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		
	    JSONArray jArray = new JSONArray();
	   
		try (Connection conn = ConnectionPool.getConnection(env.getProperty("mysql.host"), env.getProperty("mysql.db"),env.getProperty("mysql.username"))) {
	
	    	String strSQL = "{call sp_getViewCount_v1(?,?,?,?)}";
	
	        stmt = conn.prepareCall(strSQL);
	        stmt.setString(1, campaign);
	        stmt.setString(2, category);
	        stmt.setString(3, startdate);
	        stmt.setString(4, endtdate);
	        
	        boolean hadResults = stmt.execute();
	        
	        while (hadResults) {
	            //ResultSet resultSet
	        	rs = stmt.getResultSet();
	
	            // process result set
	            while (rs.next()) {
	            	JSONObject jsonobj = new JSONObject() ;
	
	            	jsonobj.put("total", rs.getInt("total"));
	            	jsonobj.put("category", rs.getString("category"));
	            	//jsonobj.put("campaignName", rs.getString("campaignName"));
	            	
	                jArray.put(jsonobj);
	            }
	
	            hadResults = stmt.getMoreResults();
	        }
	        return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	    }
	    catch(SQLException e) {
		System.err.println(e.getMessage());
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    finally{
	    	try {
	        	if (ps != null) {
	        		ps.close();
	        	}
	        	if (rs != null) {
	        		rs.close();
	        	}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	}

	@Override
	public String getEventAnalytics(String campaign, String startdate, String endtdate) {
		PreparedStatement ps = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		
	    JSONArray jArray = new JSONArray();
	   
		try (Connection conn = ConnectionPool.getConnection(env.getProperty("mysql.host"), env.getProperty("mysql.db"),env.getProperty("mysql.username"))) {
	
	    	String strSQL = "{call sp_getEventCount(?,?,?)}";
	
	        stmt = conn.prepareCall(strSQL);
	        stmt.setString(1, campaign);
	        stmt.setString(2, startdate);
	        stmt.setString(3, endtdate);
	       
	        boolean hadResults = stmt.execute();
	        
	        while (hadResults) {
	            //ResultSet resultSet
	        	rs = stmt.getResultSet();
	
	            // process result set
	            while (rs.next()) {
	            	JSONObject jsonobj = new JSONObject() ;
	
	            	jsonobj.put("eventDate", rs.getString("eventDate"));
	            	jsonobj.put("eventCampaign", rs.getString("eventCampaign"));
	            	jsonobj.put("eventClient", rs.getString("eventClient"));
	            	jsonobj.put("eventAction", rs.getString("eventAction"));
	            	jsonobj.put("eventLabel", rs.getString("eventLabel"));
	            	jsonobj.put("totalEvents", rs.getInt("totalEvents"));
	            	
	            	jArray.put(jsonobj);
	            }
	
	            hadResults = stmt.getMoreResults();
	        }
	        return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	    }
	    catch(SQLException e) {
		System.err.println(e.getMessage());
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    finally{
	    	try {
	        	if (ps != null) {
	        		ps.close();
	        	}
	        	if (rs != null) {
	        		rs.close();
	        	}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	}

	@Override
	public String getEventbyAction(String campaign, String startdate, String endtdate) {
		PreparedStatement ps = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		
	    JSONArray jArray = new JSONArray();
	   
		try (Connection conn = ConnectionPool.getConnection(env.getProperty("mysql.host"), env.getProperty("mysql.db"),env.getProperty("mysql.username"))) {
	
	    	String strSQL = "{call sp_getEventsByAction(?,?,?)}";
	
	        stmt = conn.prepareCall(strSQL);
	        stmt.setString(1, campaign);
	        stmt.setString(2, startdate);
	        stmt.setString(3, endtdate);
	       
	        boolean hadResults = stmt.execute();
	        
	        while (hadResults) {
	            //ResultSet resultSet
	        	rs = stmt.getResultSet();
	
	            // process result set
	            while (rs.next()) {
	            	JSONObject jsonobj = new JSONObject() ;
	
	            	jsonobj.put("total", rs.getInt("total"));
	            	jsonobj.put("eventAction", rs.getString("eventAction"));
	            	
	                jArray.put(jsonobj);
	            }
	
	            hadResults = stmt.getMoreResults();
	        }
	        JSONObject jsonobjFinal = new JSONObject();
	        jsonobjFinal.put("data", jArray);
	        
	        return jsonobjFinal.toString().trim().equals("[]") ? null : jsonobjFinal.toString().trim();
	        //return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	    }
	    catch(SQLException e) {
		System.err.println(e.getMessage());
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    finally{
	    	try {
	        	if (ps != null) {
	        		ps.close();
	        	}
	        	if (rs != null) {
	        		rs.close();
	        	}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	}

	@Override
	public String getAllAnalytics(String campaign, String startdate, String endtdate, String filePath) {
		return filePath;
		
		/*String filename = filePath+"analyticsReport"+".csv";

    	try (Connection conn = ConnectionPool.getConnection(host, db)){
	    	
	    	FileWriter fw = new FileWriter(filename);
	    	List<Client> clients = getClient(initialLetter,host,db,clientName);
	    	
        	String header="Customer_id|Customer_name|Video";//|ThumbNail_link";
        	fw.append(header);
        	fw.append('\n');
          	for(Client clt : clients){
			    fw.append(clt.getCustomer_id());
			    fw.append('|');
			    fw.append(clt.getCustomer_name());
			    fw.append('|');
			    //fw.append("https://pps.nvidyo.co.za/n4mvp/v2/testVideo/videoJsAutoPlayNm.html?v=/videos/pps/ins_cc/"+
			    //			clt.getVideo_file());
			    fw.append(clt.getVideo_file());
			    //fw.append('|');
			    //fw.append(clt.getThumbnail_link());
			    
			    fw.append('\n');
			    update_status_FinalCsv_create(clt.getClient_id(), clt.getProject_id(), 
			    		clt.getGenerate_id(), clt.getCustomer_id(),host,db,clientName);
          	}
	            System.out.println(" Final Dispatched CSV is created successfully.");
          	
	            fw.flush();
			    fw.close();
			    conn.close();
          	
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }*/
	}
			/********* Client methods start**********/
	@Override
	public String getCountCategory_client(String client,String campaign, String category, String startdate, String endtdate) {
		PreparedStatement ps = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		
	    JSONArray jArray = new JSONArray();
	   
		try (Connection conn = ConnectionPool.getConnection(env.getProperty("mysql.host"), env.getProperty("mysql.db"),env.getProperty("mysql.username"))) {
	
	    	String strSQL = "{call sp_getCountCategory_client(?,?,?,?,?)}";
	
	        stmt = conn.prepareCall(strSQL);
	        stmt.setString(1, client);
	        stmt.setString(2, campaign);
	        stmt.setString(3, category);
	        stmt.setString(4, startdate);
	        stmt.setString(5, endtdate);
	        
	        boolean hadResults = stmt.execute();
	        
	        while (hadResults) {
	            //ResultSet resultSet
	        	rs = stmt.getResultSet();
	
	            // process result set
	            while (rs.next()) {
	            	JSONObject jsonobj = new JSONObject() ;
	
	            	jsonobj.put("total", rs.getInt("total"));
	            	jsonobj.put("category", rs.getString("category"));
	            	//jsonobj.put("campaignName", rs.getString("campaignName"));
	            	
	                jArray.put(jsonobj);
	            }
	
	            hadResults = stmt.getMoreResults();
	        }
	        System.out.println(jArray.toString().trim().equals("[]") ? null : jArray.toString().trim());
	        return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	    }
	    catch(SQLException e) {
		System.err.println(e.getMessage());
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    finally{
	    	try {
	        	if (ps != null) {
	        		ps.close();
	        	}
	        	if (rs != null) {
	        		rs.close();
	        	}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    //System.out.println(jArray.toString().trim().equals("[]") ? null : jArray.toString().trim());
	    return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	}

	@Override
	public String getEventAnalytics_client(String client,String campaign, String startdate, String endtdate,String outType) {
		PreparedStatement ps = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		
	    JSONArray jArray = new JSONArray();
	    String csvHeader="customer_id,customer_name,eventDate,eventDateTime,eventClient,eventCampaign,eventAction,eventLabel,eventCountry,eventCity,eventBrowser,eventOperatingSystem,totalEvents\n";
	    
		try (Connection conn = ConnectionPool.getConnection(env.getProperty("mysql.host"), env.getProperty("mysql.db"),env.getProperty("mysql.username"))) {
	
	    	String strSQL = "{call sp_getEvents_client_v2(?,?,?,?)}";
	
	        stmt = conn.prepareCall(strSQL);
	        stmt.setString(1, client);
	        stmt.setString(2, campaign);
	        stmt.setString(3, startdate);
	        stmt.setString(4, endtdate);
	       
	        boolean hadResults = stmt.execute();
	        
	        while (hadResults) {
	            //ResultSet resultSet
	        	rs = stmt.getResultSet();
	        	
	            // process result set
	            while (rs.next()) {
	            	if (outType.equalsIgnoreCase("json")){
	            		JSONObject jsonobj = new JSONObject() ;
	            		
	            		jsonobj.put("customer_id", rs.getString("customer_id"));
	            		jsonobj.put("customer_name", rs.getString("customer_name"));
		            	jsonobj.put("eventDate", rs.getString("eventDate"));
		            	jsonobj.put("eventDateTime", rs.getString("eventDateTime"));
		            	jsonobj.put("eventCampaign", rs.getString("eventCampaign"));
		            	jsonobj.put("eventClient", rs.getString("eventClient"));
		            	jsonobj.put("eventAction", rs.getString("eventAction"));
		            	jsonobj.put("eventLabel", rs.getString("eventLabel"));
		            	jsonobj.put("eventCountry", rs.getString("eventCountry"));
		            	jsonobj.put("eventCity", rs.getString("eventCity"));
		            	jsonobj.put("eventBrowser", rs.getString("eventBrowser"));
		            	jsonobj.put("eventOperatingSystem", rs.getString("eventOperatingSystem"));
		            	jsonobj.put("totalEvents", rs.getInt("totalEvents"));
		            	
		            	jArray.put(jsonobj);
	            	}else{
	            		csvHeader+=rs.getString("customer_id")+","+rs.getString("customer_name")+","+rs.getString("eventDate")+","+rs.getString("eventDateTime")+","+rs.getString("eventClient")+","+
	            				rs.getString("eventCampaign")+","+rs.getString("eventAction")+","+rs.getString("eventLabel")+","+rs.getString("eventCountry")+","+
	            				rs.getString("eventCity")+","+rs.getString("eventBrowser")+","+rs.getString("eventOperatingSystem")+","+rs.getInt("totalEvents")+"\n";
	            	}
	            	
	            }
	            hadResults = stmt.getMoreResults();
	        }
	        //System.out.println(jArray.toString().trim().equals("[]") ? null : jArray.toString().trim());
	        if (outType.equalsIgnoreCase("json")){
	        	return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	        	
	        }else{
	        	return csvHeader;
	        }
	        
	    }
	    catch(SQLException e) {
		System.err.println(e.getMessage());
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    finally{
	    	try {
	        	if (ps != null) {
	        		ps.close();
	        	}
	        	if (rs != null) {
	        		rs.close();
	        	}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	}

	@Override
	public String getEventbyAction_client(String client,String campaign, String startdate, String endtdate) {
		PreparedStatement ps = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		
	    JSONArray jArray = new JSONArray();
	   
		try (Connection conn = ConnectionPool.getConnection(env.getProperty("mysql.host"), env.getProperty("mysql.db"),env.getProperty("mysql.username"))) {
	
	    	String strSQL = "{call sp_getEventsCountByAction_client_v2(?,?,?,?)}";
	
	        stmt = conn.prepareCall(strSQL);
	        stmt.setString(1, client);
	        stmt.setString(2, campaign);
	        stmt.setString(3, startdate);
	        stmt.setString(4, endtdate);
	       
	        boolean hadResults = stmt.execute();
	        
	        while (hadResults) {
	            //ResultSet resultSet
	        	rs = stmt.getResultSet();
	
	            // process result set
	            while (rs.next()) {
	            	JSONObject jsonobj = new JSONObject() ;
	
	            	jsonobj.put("total", rs.getInt("total"));
	            	jsonobj.put("eventAction", rs.getString("eventAction"));
	            	
	                jArray.put(jsonobj);
	            }
	
	            hadResults = stmt.getMoreResults();
	        }
	        JSONObject jsonobjFinal = new JSONObject();
	        jsonobjFinal.put("data", jArray);
	        
	        return jsonobjFinal.toString().trim().equals("[]") ? null : jsonobjFinal.toString().trim();
	        //return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	    }
	    catch(SQLException e) {
		System.err.println(e.getMessage());
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    finally{
	    	try {
	        	if (ps != null) {
	        		ps.close();
	        	}
	        	if (rs != null) {
	        		rs.close();
	        	}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	}

	/*@Override
	public String getVideosByDate_orig(String client,String campaign, String startdate, String endtdate, String outType) {
		PreparedStatement ps = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		
	    JSONArray jArray = new JSONArray();
	    String csvHeader= "Videos,Date\n";
		try (Connection conn = ConnectionPool.getConnection(env.getProperty("mysql.host"), env.getProperty("mysql.db"),env.getProperty("mysql.username"))) {
	
	    	String strSQL = "{call sp_getDistinctEventsLabelByDate_client(?,?,?,?)}";
	
	        stmt = conn.prepareCall(strSQL);
	        stmt.setString(1, client);
	        stmt.setString(2, campaign);
	        stmt.setString(3, startdate);
	        stmt.setString(4, endtdate);
	       
	        boolean hadResults = stmt.execute();
	        
	        while (hadResults) {
	            //ResultSet resultSet
	        	rs = stmt.getResultSet();
	
	            // process result set
	            while (rs.next()) {
	            	if (outType.equalsIgnoreCase("json")){
		            	JSONObject jsonobj = new JSONObject() ;
		
		            	jsonobj.put("eventLabel", rs.getString("eventLabel"));
		            	jsonobj.put("date", rs.getString("eventdate"));
		            	
		                jArray.put(jsonobj);
		            }else{
	            		csvHeader+=rs.getString("eventLabel")+","+rs.getString("eventdate")+"\n";
	            	}
	            }	
		        hadResults = stmt.getMoreResults();
	            
            }if (outType.equalsIgnoreCase("json")){
            	return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	        	
	        }else{
	        	return csvHeader;
	        }
	    }
	    catch(SQLException e) {
		System.err.println(e.getMessage());
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    finally{
	    	try {
	        	if (ps != null) {
	        		ps.close();
	        	}
	        	if (rs != null) {
	        		rs.close();
	        	}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	}*/

	@Override
	public String getVideosByDate(String client,String campaign, String startdate, String endtdate, String outType) {
		PreparedStatement ps = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		
	    JSONArray jArray = new JSONArray();
	    String csvHeader= "Customer Id,Customer Name,Video File\n";
		try (Connection conn = ConnectionPool.getConnection(env.getProperty("mysql.host"), env.getProperty("mysql.db"),env.getProperty("mysql.username"))) {
	
	    	//String strSQL = client == "pps" ?  "{call sp_getVideosViewCount_client_v1(?,?,?,?)}" :  "{call sp_getVideosViewed_client(?,?,?,?)}";
			String strSQL = "{call sp_getVideosViewCount_client_v1(?,?,?,?)}";
	        stmt = conn.prepareCall(strSQL);
	        stmt.setString(1, client);
	        stmt.setString(2, campaign);
	        stmt.setString(3, startdate);
	        stmt.setString(4, endtdate);
	       
	        boolean hadResults = stmt.execute();
	        
	        while (hadResults) {
	            //ResultSet resultSet
	        	rs = stmt.getResultSet();
	
	            // process result set
	            while (rs.next()) {
	            	if (outType.equalsIgnoreCase("json")){
		            	JSONObject jsonobj = new JSONObject() ;
		
		            	jsonobj.put("customer_id", rs.getString("customer_id"));
		            	jsonobj.put("customer_name", rs.getString("customer_name"));
		            	jsonobj.put("video_file", rs.getString("video_file"));
		                jArray.put(jsonobj);
		            }else{
	            		csvHeader+=rs.getString("customer_id")+","+rs.getString("customer_name")+","+
	            				rs.getString("video_file")+"\n";
	            	}
	            }	
		        hadResults = stmt.getMoreResults();
	            
            }if (outType.equalsIgnoreCase("json")){
            	return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	        	
	        }else{
	        	return csvHeader;
	        }
	    }
	    catch(SQLException e) {
		System.err.println(e.getMessage());
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    finally{
	    	try {
	        	if (ps != null) {
	        		ps.close();
	        	}
	        	if (rs != null) {
	        		rs.close();
	        	}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	}
	
	@Override
	public String getEventSummary_client(String client,String campaign, String startdate, String endtdate, String outType) {
		PreparedStatement ps = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		
	    JSONArray jArray = new JSONArray();
	    String csvHeader= "Customer Id, Customer Name,Date,Date Time,Videos,Country,City,Browser,Opearating System\n";
		try (Connection conn = ConnectionPool.getConnection(env.getProperty("mysql.host"), env.getProperty("mysql.db"),env.getProperty("mysql.username"))) {
	
	    	String strSQL = "{call sp_getAllEventSummary_client_v2(?,?,?,?)}";
	
	        stmt = conn.prepareCall(strSQL);
	        stmt.setString(1, client);
	        stmt.setString(2, campaign);
	        stmt.setString(3, startdate);
	        stmt.setString(4, endtdate);
	       
	        boolean hadResults = stmt.execute();
	        
	        while (hadResults) {
	            //ResultSet resultSet
	        	rs = stmt.getResultSet();
	        	
	            // process result set
	            while (rs.next()) {
	            	if (outType.equalsIgnoreCase("json")){
		            	JSONObject jsonobj = new JSONObject() ;
		            	
		            	jsonobj.put("customer_id", rs.getString("customer_id"));
		            	jsonobj.put("customer_name", rs.getString("customer_name"));
		            	jsonobj.put("eventDate", rs.getString("eventDate"));
		            	jsonobj.put("eventDateTime", rs.getString("eventDateTime"));
		            	jsonobj.put("eventLabel", rs.getString("eventLabel"));
		            	jsonobj.put("eventCountry", rs.getString("eventCountry"));
		            	jsonobj.put("eventCity", rs.getString("eventCity"));
		            	jsonobj.put("eventBrowser", rs.getString("eventBrowser"));
		            	jsonobj.put("eventOperatingSystem", rs.getString("eventOperatingSystem"));
		            	
		            	jArray.put(jsonobj);
	            	}else{
	            		csvHeader+=rs.getString("customer_id")+","+rs.getString("customer_name")+","+
	            				rs.getString("eventDate")+","+rs.getString("eventDateTime")+
	            				rs.getString("eventLabel")+","+rs.getString("eventCountry")+","+
	            				rs.getString("eventCity")+","+rs.getString("eventBrowser")+","+
	            				rs.getString("eventOperatingSystem")+"\n";
	            	}
	            }
	            hadResults = stmt.getMoreResults();
	        }if (outType.equalsIgnoreCase("json")){
            	return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	        	
	        }else{
	        	return csvHeader;
	        }
	    }
	    catch(SQLException e) {
		System.err.println(e.getMessage());
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    finally{
	    	try {
	        	if (ps != null) {
	        		ps.close();
	        	}
	        	if (rs != null) {
	        		rs.close();
	        	}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	}

	@Override
	public String videosNotViewed(String client,String campaign, String startdate, String endtdate, String outType) {
		PreparedStatement ps = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		
	    JSONArray jArray = new JSONArray();
	    String csvHeader= "Customer Id,Customer Name,Video File\n";
		try (Connection conn = ConnectionPool.getConnection(env.getProperty("mysql.host"), env.getProperty("mysql.db"),env.getProperty("mysql.username"))) {
	
	    	String strSQL = "{call sp_getVideosNotViewed_client(?,?,?,?)}";
	
	        stmt = conn.prepareCall(strSQL);
	        stmt.setString(1, client);
	        stmt.setString(2, campaign);
	        stmt.setString(3, startdate);
	        stmt.setString(4, endtdate);
	       
	        boolean hadResults = stmt.execute();
	        
	        while (hadResults) {
	            //ResultSet resultSet
	        	rs = stmt.getResultSet();
	
	            // process result set
	            while (rs.next()) {
	            	if (outType.equalsIgnoreCase("json")){
		            	JSONObject jsonobj = new JSONObject() ;
		
		            	jsonobj.put("customer_id", rs.getString("customer_id"));
		            	jsonobj.put("customer_name", rs.getString("customer_name"));
		            	jsonobj.put("video_file", rs.getString("video_file"));
		                jArray.put(jsonobj);
		            }else{
	            		csvHeader+=rs.getString("customer_id")+","+rs.getString("customer_name")+","+
	            				rs.getString("video_file")+"\n";
	            	}
	            }	
		        hadResults = stmt.getMoreResults();
	            
            }if (outType.equalsIgnoreCase("json")){
            	return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	        	
	        }else{
	        	return csvHeader;
	        }
	    }
	    catch(SQLException e) {
		System.err.println(e.getMessage());
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    finally{
	    	try {
	        	if (ps != null) {
	        		ps.close();
	        	}
	        	if (rs != null) {
	        		rs.close();
	        	}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	}

	@Override
	public String getVideosViewCount_client(String client,String campaign, String startdate, String endtdate) {
		PreparedStatement ps = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		
	    JSONArray jArray = new JSONArray();
	   
		try (Connection conn = ConnectionPool.getConnection(env.getProperty("mysql.host"), env.getProperty("mysql.db"),env.getProperty("mysql.username"))) {
	
	    	String strSQL = "{call sp_getVideosViewCount_client(?,?,?,?)}";
	
	        stmt = conn.prepareCall(strSQL);
	        stmt.setString(1, client);
	        stmt.setString(2, campaign);
	        stmt.setString(3, startdate);
	        stmt.setString(4, endtdate);
	       
	        boolean hadResults = stmt.execute();
	        JSONObject jsonobj = new JSONObject() ;
	        while (hadResults) {
	            //ResultSet resultSet
	        	rs= stmt.getResultSet();
	
	            // process result set
	            while (rs.next()) {
	            	String colName = rs.getMetaData().getColumnLabel(1);
	            	jsonobj.put(colName, rs.getInt(colName));
		          }
	
	            hadResults = stmt.getMoreResults();
	        }
	       
	        return jsonobj.toString().trim().equals("[]") ? null : jsonobj.toString().trim();
	    }
	    catch(SQLException e) {
		System.err.println(e.getMessage());
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    finally{
	    	try {
	        	if (ps != null) {
	        		ps.close();
	        	}
	        	if (rs != null) {
	        		rs.close();
	        	}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	}

	@Override
	public String getClientCampaign(String userName) {
		PreparedStatement ps = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		
	    JSONArray jArray = new JSONArray();
	   
		try (Connection conn = ConnectionPool.getConnection(env.getProperty("mysql.host"), env.getProperty("mysql.db"),env.getProperty("mysql.username"))) {
	
	    	String strSQL = "{call sp_getClientCampaign(?)}";
	
	    	stmt = conn.prepareCall(strSQL);
	        stmt.setString(1, userName);
	    	
	        boolean hadResults = stmt.execute();
	        
	        while (hadResults) {
	            //ResultSet resultSet
	        	rs = stmt.getResultSet();
	
	            // process result set
	            while (rs.next()) {
	            	JSONObject jsonobj = new JSONObject() ;
	
	            	jsonobj.put("project_id", rs.getInt("project_id"));
	            	jsonobj.put("project_details", rs.getString("project_details"));
	            	
	                jArray.put(jsonobj);
	            }
	
	            hadResults = stmt.getMoreResults();
	        }
	        System.out.println(jArray.toString().trim().equals("[]") ? null : jArray.toString().trim());
	        return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	    }
	    catch(SQLException e) {
		System.err.println(e.getMessage());
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    finally{
	    	try {
	        	if (ps != null) {
	        		ps.close();
	        	}
	        	if (rs != null) {
	        		rs.close();
	        	}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    //System.out.println(jArray.toString().trim().equals("[]") ? null : jArray.toString().trim());
	    return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	}

	@Override
	public String getlandingPage(String userName, String password) {
		
		PreparedStatement ps = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		
	    JSONArray jArray = new JSONArray();
	   
		try (Connection conn = ConnectionPool.getConnection(env.getProperty("mysql.host"), env.getProperty("mysql.db"),env.getProperty("mysql.username"))) {
	
	    	String strSQL = "{call sp_getLandingPage(?,?)}";
	
	    	stmt = conn.prepareCall(strSQL);
	        stmt.setString(1, userName);
	        stmt.setString(2, password);
	    	
	        boolean hadResults = stmt.execute();
	        
	        while (hadResults) {
	            //ResultSet resultSet
	        	rs = stmt.getResultSet();
	
	            // process result set
	            while (rs.next()) {
	            	JSONObject jsonobj = new JSONObject() ;
	
	            	jsonobj.put("landing_page", rs.getString("landing_page"));
	            	
	                jArray.put(jsonobj);
	            }
	
	            hadResults = stmt.getMoreResults();
	        }
	        //System.out.println(jArray.toString().trim().equals("[]") ? null : jArray.toString().trim());
	        return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	    }
	    catch(SQLException e) {
		System.err.println(e.getMessage());
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    finally{
	    	try {
	        	if (ps != null) {
	        		ps.close();
	        	}
	        	if (rs != null) {
	        		rs.close();
	        	}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    //System.out.println(jArray.toString().trim().equals("[]") ? null : jArray.toString().trim());
	    return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	}

	@Override
	public String getclientName(String userName) {
		
		PreparedStatement ps = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		
	    JSONArray jArray = new JSONArray();
	   
		try (Connection conn = ConnectionPool.getConnection(env.getProperty("mysql.host"), env.getProperty("mysql.db"),env.getProperty("mysql.username"))) {
	
	    	String strSQL = "{call sp_getClientName(?)}";
	
	    	stmt = conn.prepareCall(strSQL);
	        stmt.setString(1, userName);
	     
	        boolean hadResults = stmt.execute();
	        
	        while (hadResults) {
	            //ResultSet resultSet
	        	rs = stmt.getResultSet();
	
	            // process result set
	            while (rs.next()) {
	            	JSONObject jsonobj = new JSONObject() ;
	
	            	jsonobj.put("client_name", rs.getString("client_name"));
	            	
	                jArray.put(jsonobj);
	            }
	
	            hadResults = stmt.getMoreResults();
	        }
	        //System.out.println(jArray.toString().trim().equals("[]") ? null : jArray.toString().trim());
	        return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	    }
	    catch(SQLException e) {
		System.err.println(e.getMessage());
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    finally{
	    	try {
	        	if (ps != null) {
	        		ps.close();
	        	}
	        	if (rs != null) {
	        		rs.close();
	        	}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    //System.out.println(jArray.toString().trim().equals("[]") ? null : jArray.toString().trim());
	    return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	}

	@Override
	public String videosViewedByPercent(String client,String campaign, String startdate, String endtdate, String outType) {
		PreparedStatement ps = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		
	    JSONArray jArray = new JSONArray();
	    String csvHeader= "Customer Id,Customer Name,Video File,Percent Played\n";
		try (Connection conn = ConnectionPool.getConnection(env.getProperty("mysql.host"), env.getProperty("mysql.db"),env.getProperty("mysql.username"))) {
	
	    	String strSQL = "{call sp_getVideos_client_percentage(?,?,?,?)}";
	
	        stmt = conn.prepareCall(strSQL);
	        stmt.setString(1, client);
	        stmt.setString(2, campaign);
	        stmt.setString(3, startdate);
	        stmt.setString(4, endtdate);
	       
	        boolean hadResults = stmt.execute();
	        
	        while (hadResults) {
	            //ResultSet resultSet
	        	rs = stmt.getResultSet();
	
	            // process result set
	            while (rs.next()) {
	            	if (outType.equalsIgnoreCase("json")){
		            	JSONObject jsonobj = new JSONObject() ;
		
		            	jsonobj.put("customer_id", rs.getString("customer_id"));
		            	jsonobj.put("customer_name", rs.getString("customer_name"));
		            	jsonobj.put("video_file", rs.getString("video_file"));
		            	jsonobj.put("eventAction", rs.getString("eventAction"));
		                jArray.put(jsonobj);
		            }else{
	            		csvHeader+=rs.getString("customer_id")+","+rs.getString("customer_name")+","+
	            				rs.getString("video_file")+rs.getString("eventAction")+"\n";
	            	}
	            }	
		        hadResults = stmt.getMoreResults();
	            
            }if (outType.equalsIgnoreCase("json")){
            	return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	        	
	        }else{
	        	return csvHeader;
	        }
	    }
	    catch(SQLException e) {
		System.err.println(e.getMessage());
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    finally{
	    	try {
	        	if (ps != null) {
	        		ps.close();
	        	}
	        	if (rs != null) {
	        		rs.close();
	        	}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    return jArray.toString().trim().equals("[]") ? null : jArray.toString().trim();
	}
}
