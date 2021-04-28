package org.nvidyo.webapp.npv;

import java.util.List;

import org.json.JSONException;

public interface NpvService {
     
byte[] synthesizeSsml(String ssml, String gender, String language) throws JSONException;

String getAnalyticsData(String starttdate, String endtdate);

String getCount(String campaign, String category,String startdate, String endtdate);

String getEventAnalytics(String campaign, String startdate, String endtdate);

String getEventbyAction(String campaign, String startdate, String endtdate);

String getAllAnalytics(String campaign, String startdate, String endtdate, String filePath);

String getCountCategory_client(String client,String campaign, String category, String startdate, String endtdate);

String getEventAnalytics_client(String client,String campaign, String startdate, String endtdate,String outType);

String getEventbyAction_client(String client,String campaign, String startdate, String endtdate);

String getVideosByDate(String client,String campaign, String startdate, String endtdate, String outType);

String getEventSummary_client(String client,String campaign, String startdate, String endtdate, String outType);

String videosNotViewed(String client,String campaign, String startdate, String endtdate, String string);

String getVideosViewCount_client(String client,String campaign, String startdate, String endtdate);

String getClientCampaign(String userName);

String getlandingPage(String userName, String password);

String getclientName(String userName);

String videosViewedByPercent(String client, String campaign, String startdate, String endtdate, String string);

     
}