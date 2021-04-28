package org.nvidyo.webapp.npv;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.net.MediaType;
import org.apache.commons.codec.binary.Base64;

//@CrossOrigin //(origins = "http://localhost:8080", maxAge = 3600)
@RestController
@RequestMapping(value="/npv")
public class NpvController {
	
	@Autowired
    NpvService npvService;
    
    @RequestMapping(value = "/tts/{gender}/{language}", method = RequestMethod.PUT, produces ="application/json")
	@ResponseStatus(HttpStatus.OK)
    public String synthesizeSsml(
    		@PathVariable("gender") String gender,
    		@PathVariable("language") String language,
    	//@RequestHeader(value="ssmlTxt") String ssml) throws JSONException{
        @RequestBody String ssml) throws JSONException, FileNotFoundException, IOException{
    	byte[] audio = npvService.synthesizeSsml(ssml,gender,language);
        
        //return new ResponseEntity<byte[]>(audio,HttpStatus.OK);
    	try (OutputStream out = new FileOutputStream("myAudio.mp3")) {
  	      out.write(audio);
  	      System.out.println("Audio content written to file \"myAudio.mp3\"");
  	      //String message = Base64.encodeBase64String(audio);
  	      return Base64.encodeBase64String(audio);
    	}
    }
    
    @RequestMapping(value = "/chart/{startdate}/{endtdate}", method = RequestMethod.GET, produces ="application/json")
	@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> getUsers(
    		@PathVariable("startdate") String startdate,
    		@PathVariable("endtdate") String endtdate) {

        String data = npvService.getAnalyticsData(startdate,endtdate);
        if (data == null) {
            System.out.println("getAnalytics Data not successful");
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(data, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/chart/{campaign}/{category}/{startdate}/{endtdate}", method = RequestMethod.GET, produces ="application/json")
	@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String>getCount(
    		@PathVariable("campaign") String campaign,
    		@PathVariable("category") String category,
    		@PathVariable("startdate") String startdate,
    		@PathVariable("endtdate") String endtdate) {

        String data = npvService.getCount(campaign,category,startdate,endtdate);
        if (data == null) {
            System.out.println("getAnalytics Data not successful");
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(data, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/chart/events/{campaign}/{startdate}/{endtdate}", method = RequestMethod.GET, produces ="application/json")
	@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String>getEventAnalytics(
    		@PathVariable("campaign") String campaign,
		    @PathVariable("startdate") String startdate,
			@PathVariable("endtdate") String endtdate) {

        String data = npvService.getEventAnalytics(campaign,startdate,endtdate);
        if (data == null) {
            System.out.println("geteventsAnalytics Data not successful");
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(data, HttpStatus.OK);
    }
   
    @RequestMapping(value = "/chart/events/action/{campaign}/{startdate}/{endtdate}", method = RequestMethod.GET, produces ="application/json")
	@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String>getEventbyAction(
    		@PathVariable("campaign") String campaign,
		    @PathVariable("startdate") String startdate,
			@PathVariable("endtdate") String endtdate) {

        String data = npvService.getEventbyAction(campaign,startdate,endtdate);
        if (data == null) {
            System.out.println("geteventsByAction Data not successful");
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(data, HttpStatus.OK);
    }
//
    @RequestMapping(value = "/client/chart/{client}/{campaign}/{category}/{startdate}/{endtdate}", method = RequestMethod.GET, produces ="application/json")
	@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String>getCountCategory_client(
    		@PathVariable("client") String client,
    		@PathVariable("campaign") String campaign,
    		@PathVariable("category") String category,
    		@PathVariable("startdate") String startdate,
    		@PathVariable("endtdate") String endtdate) {

        String data = npvService.getCountCategory_client(client,campaign,category,startdate,endtdate);
        if (data == null) {
            System.out.println("getCountCategory_client Data not successful");
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(data, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/client/events/{client}/{campaign}/{startdate}/{endtdate}", method = RequestMethod.GET, produces ="application/json")
	@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String>getEvent_client(
    		@PathVariable("client") String client,
    		@PathVariable("campaign") String campaign,
		    @PathVariable("startdate") String startdate,
			@PathVariable("endtdate") String endtdate) {

        String data = npvService.getEventAnalytics_client(client,campaign,startdate,endtdate,"json");
        if (data == null) {
            System.out.println("getEvent_client Data not successful");
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(data, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/client/exportevents/{client}/{campaign}/{startdate}/{endtdate}", method = RequestMethod.GET, produces ="application/json")
	@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String>exportEvent_client(
    		@PathVariable("client") String client,
    		@PathVariable("campaign") String campaign,
		    @PathVariable("startdate") String startdate,
			@PathVariable("endtdate") String endtdate) {

        String data = npvService.getEventAnalytics_client(client,campaign,startdate,endtdate,"csv");
        if (data == null) {
            System.out.println("getEventAnalytics_client Data not successful");
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(data, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/client/eventSummary/{client}/{campaign}/{startdate}/{endtdate}", method = RequestMethod.GET, produces ="application/json")
	@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String>getEventSummary_client(
    		@PathVariable("client") String client,
    		@PathVariable("campaign") String campaign,
		    @PathVariable("startdate") String startdate,
			@PathVariable("endtdate") String endtdate) {

        String data = npvService.getEventSummary_client(client,campaign,startdate,endtdate,"json");
        if (data == null) {
            System.out.println("getEventSummary_client Data not successful");
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(data, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/client/exportEventSummary/{client}/{campaign}/{startdate}/{endtdate}", method = RequestMethod.GET, produces ="application/json")
	@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String>exportEventSummary_client(
    		@PathVariable("client") String client,
    		@PathVariable("campaign") String campaign,
		    @PathVariable("startdate") String startdate,
			@PathVariable("endtdate") String endtdate) {

        String data = npvService.getEventSummary_client(client,campaign,startdate,endtdate,"csv");
        if (data == null) {
            System.out.println("getEventSummary_client Data not successful");
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(data, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/client/exportUniqueVideos/{client}/{campaign}/{startdate}/{endtdate}", method = RequestMethod.GET, produces ="application/json")
	@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String>exportVideosByDate(
    		@PathVariable("client") String client,
    		@PathVariable("campaign") String campaign,
		    @PathVariable("startdate") String startdate,
			@PathVariable("endtdate") String endtdate) {

        String data = npvService.getVideosByDate(client,campaign,startdate,endtdate,"csv");
        if (data == null) {
            System.out.println("getVideosByDate Data not successful");
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(data, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/client/uniqueVideos/{client}/{campaign}/{startdate}/{endtdate}", method = RequestMethod.GET, produces ="application/json")
	@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String>getVideosByDate(
    		@PathVariable("client") String client,
    		@PathVariable("campaign") String campaign,
		    @PathVariable("startdate") String startdate,
			@PathVariable("endtdate") String endtdate) {

        String data = npvService.getVideosByDate(client,campaign,startdate,endtdate,"json");
        if (data == null) {
            System.out.println("getVideosByDate Data not successful");
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(data, HttpStatus.OK);
    }
    @RequestMapping(value = "/client/chart/events/action/{client}/{campaign}/{startdate}/{endtdate}", method = RequestMethod.GET, produces ="application/json")
	@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String>getEventbyAction_client(
    		@PathVariable("client") String client,
    		@PathVariable("campaign") String campaign,
		    @PathVariable("startdate") String startdate,
			@PathVariable("endtdate") String endtdate) {

        String data = npvService.getEventbyAction_client(client,campaign,startdate,endtdate);
        if (data == null) {
            System.out.println("getEventbyAction_client Data not successful");
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(data, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/client/videosNotViewed/{client}/{campaign}/{startdate}/{endtdate}", method = RequestMethod.GET, produces ="application/json")
	@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String>videosNotViewed(
    		@PathVariable("client") String client,
    		@PathVariable("campaign") String campaign,
		    @PathVariable("startdate") String startdate,
			@PathVariable("endtdate") String endtdate) {

        String data = npvService.videosNotViewed(client,campaign,startdate,endtdate,"json");
        if (data == null) {
            System.out.println("videosNotViewed Data not successful");
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(data, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/client/exportVideosNotViewed/{client}/{campaign}/{startdate}/{endtdate}", method = RequestMethod.GET, produces ="application/json")
	@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String>exportVideosNotViewed(
    		@PathVariable("client") String client,
    		@PathVariable("campaign") String campaign,
		    @PathVariable("startdate") String startdate,
			@PathVariable("endtdate") String endtdate) {

        String data = npvService.videosNotViewed(client,campaign,startdate,endtdate,"csv");
        if (data == null) {
            System.out.println("exportVideosNotViewed Data not successful");
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(data, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/client/videosViewedByPercent/{client}/{campaign}/{startdate}/{endtdate}", method = RequestMethod.GET, produces ="application/json")
	@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String>videosViewedByPercent(
    		@PathVariable("client") String client,
    		@PathVariable("campaign") String campaign,
		    @PathVariable("startdate") String startdate,
			@PathVariable("endtdate") String endtdate) {

        String data = npvService.videosViewedByPercent(client,campaign,startdate,endtdate,"json");
        if (data == null) {
            System.out.println("videosViewedByPercent Data not successful");
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(data, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/client/exportVideosViewedByPercent/{client}/{campaign}/{startdate}/{endtdate}", method = RequestMethod.GET, produces ="application/json")
	@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String>exportVideosViewedByPercent(
    		@PathVariable("client") String client,
    		@PathVariable("campaign") String campaign,
		    @PathVariable("startdate") String startdate,
			@PathVariable("endtdate") String endtdate) {

        String data = npvService.videosViewedByPercent(client,campaign,startdate,endtdate,"csv");
        if (data == null) {
            System.out.println("exportVideosNotViewed Data not successful");
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(data, HttpStatus.OK);
    }
    @RequestMapping(value = "/client/videosViewCount/{client}/{campaign}/{startdate}/{endtdate}", method = RequestMethod.GET, produces ="application/json")
	@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String>getVideosViewCount_client(
    		@PathVariable("client") String client,
    		@PathVariable("campaign") String campaign,
		    @PathVariable("startdate") String startdate,
			@PathVariable("endtdate") String endtdate) {

        String data = npvService.getVideosViewCount_client(client,campaign,startdate,endtdate);
        if (data == null) {
            System.out.println("getVideosViewCount_client Data not successful");
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(data, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/campaign/{userName}", method = RequestMethod.GET, produces ="application/json")
	@ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String>getClientCampaign(
    		@PathVariable("userName") String userName) {

        String data = npvService.getClientCampaign(userName);
        if (data == null) {
            System.out.println("getClientCampaign Data not successful");
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(data, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/login/{userName}/{password}", method = RequestMethod.GET, produces ="application/json")
   	@ResponseStatus(HttpStatus.OK)
       public ResponseEntity<String>getlandingPage(
       		@PathVariable("userName") String userName,
       		@PathVariable("password") String password) {

           String data = npvService.getlandingPage(userName,password);
           if (data == null) {
               System.out.println("getlandingPage Data not successful");
               return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
           }
           return new ResponseEntity<String>(data, HttpStatus.OK);
       }
    
    @RequestMapping(value = "/client/{userName}", method = RequestMethod.GET, produces ="application/json")
   	@ResponseStatus(HttpStatus.OK)
       public ResponseEntity<String>getclientName(
       		@PathVariable("userName") String userName) {

           String data = npvService.getclientName(userName);
           if (data == null) {
               System.out.println("getclientName Data not successful");
               return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
           }
           return new ResponseEntity<String>(data, HttpStatus.OK);
       }
    /*@RequestMapping(value = "/chart/events/action/{campaign}/{startdate}/{endtdate}/{filePath}", method = RequestMethod.GET, produces ="application/json")
	@ResponseStatus(HttpStatus.OK)
    public <npvService> ResponseEntity<String>getAllAnalytics(
    		@PathVariable("campaign") String campaign,
		    @PathVariable("startdate") String startdate,
			@PathVariable("endtdate") String endtdate,
			@PathVariable("filePath") String filePath) {

        String data = npvService.getAllAnalytics(campaign,startdate,endtdate,filePath);
        if (data == null) {
            System.out.println("getAllAnalytics Data not successful");
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(data, HttpStatus.OK);
    }*/
    
    @RequestMapping(value = "/n" , method = RequestMethod.GET  )
	public String sample()
	{
    	StringBuilder sb = new StringBuilder();
    	sb.append("NPV audit home page");
    	sb.append("</br>");
    	sb.append("</br>");
    	sb.append("/npv/data/{filter}");
    	sb.append("</br>");
    	sb.append("/npv/audit/save/{auditJson:.+}");
    	sb.append("</br>");

	    return sb.toString();

	}
}
