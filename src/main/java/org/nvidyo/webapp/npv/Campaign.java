package org.nvidyo.webapp.npv;

import java.sql.Timestamp;
import java.util.Date;

public class Campaign {

	private String campaignName;
	private String pagePath;
	private int pageView;
	private String avgTimeOnPage;
	private String date;
	private String dateTime;
	private String ClientName;
	private String country;
	private String region;
	private String city;
	private double longitude;
	private double latitude;
	private String eventAction;
	private String eventlabel;
	private int totalEvents;
	private String eventDate;
	private String eventdateTime;
	private String eventCampaign;
	private String eventClient;
	private int eventPercentRank;
	
	
	public String getCampaignName() {
		return campaignName;
	}
	
	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}
	
	public int getPageView() {
		return pageView;
	}
	
	public void setPageView(int pageView) {
		this.pageView = pageView;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}

	public String getPagePath() {
		return pagePath;
	}

	public void setPagePath(String pagePath) {
		this.pagePath = pagePath;
	}

	public String getClientName() {
		return ClientName;
	}

	public void setClientName(String clientName) {
		ClientName = clientName;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getEventAction() {
		return eventAction;
	}

	public void setEventAction(String eventAction) {
		this.eventAction = eventAction;
	}

	public String getEventlabel() {
		return eventlabel;
	}

	public void setEventlabel(String eventlabel) {
		this.eventlabel = eventlabel;
	}

	public int getTotalEvents() {
		return totalEvents;
	}

	public void setTotalEvents(int totalEvents) {
		this.totalEvents = totalEvents;
	}

	public String getEventDate() {
		return eventDate;
	}

	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}

	public String getEventClient() {
		return eventClient;
	}

	public void setEventClient(String eventClient) {
		this.eventClient = eventClient;
	}

	public String getEventCampaign() {
		return eventCampaign;
	}

	public void setEventCampaign(String eventCampaign) {
		this.eventCampaign = eventCampaign;
	}

	public String getAvgTimeOnPage() {
		return avgTimeOnPage;
	}

	public void setAvgTimeOnPage(String avgTimeOnPage) {
		this.avgTimeOnPage = avgTimeOnPage;
	}

	public String getEventdateTime() {
		return eventdateTime;
	}

	public void setEventdateTime(String eventdateTime) {
		this.eventdateTime = eventdateTime;
	}

	public int getEventPercentRank() {
		return eventPercentRank;
	}

	public void setEventPercentRank(int eventPercentRank) {
		this.eventPercentRank = eventPercentRank;
	}

	
	
}
