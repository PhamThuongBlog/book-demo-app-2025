package service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AppointmentAPI {
	private static final String BASE_URL = "http://localhost:8081/api";
	private static String jwtToken;

	public static void setAuthToken(String token) {
		jwtToken = token;
	}

	public static boolean login(String username, String password) throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(BASE_URL + "/auth/signin");

		JSONObject json = new JSONObject();
		json.put("username", username);
		json.put("password", password);

		StringEntity entity = new StringEntity(json.toString());
		httpPost.setEntity(entity);
		httpPost.setHeader("Content-type", "application/json");

		CloseableHttpResponse response = client.execute(httpPost);

		if (response.getStatusLine().getStatusCode() == 200) {
			String responseString = EntityUtils.toString(response.getEntity());
			JSONObject responseJson = new JSONObject(responseString);

			System.out.println("Login response: " + responseString);

			if (responseJson.has("token")) {
				jwtToken = responseJson.getString("token");
				return true;
			}
		}
		return false;
	}

	public static boolean register(String username, String email, String password, String confirmPassword)
			throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(BASE_URL + "/auth/signup");

		JSONObject json = new JSONObject();
		json.put("username", username);
		json.put("email", email);
		json.put("password", password);
		json.put("confirmPassword", confirmPassword);

		StringEntity entity = new StringEntity(json.toString());
		httpPost.setEntity(entity);
		httpPost.setHeader("Content-type", "application/json");

		CloseableHttpResponse response = client.execute(httpPost);
		return response.getStatusLine().getStatusCode() == 200;
	}

	public static JSONArray getAppointments() throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(BASE_URL + "/appointments");
		httpGet.setHeader("Authorization", "Bearer " + jwtToken);

		CloseableHttpResponse response = client.execute(httpGet);
		String responseString = EntityUtils.toString(response.getEntity()).trim();

		System.out.println("Appointments API response: " + responseString);

		try {
			return new JSONArray(responseString);
		} catch (JSONException e) {
			System.err.println("Không thể parse JSONArray: " + responseString);
			return new JSONArray();
		}
	}

	public static boolean addAppointment(String title, String startTime, String endTime, String location,
			String description) throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(BASE_URL + "/appointments");
		httpPost.setHeader("Authorization", "Bearer " + jwtToken);
		httpPost.setHeader("Content-type", "application/json");

		JSONObject json = new JSONObject();
		json.put("title", title);
		json.put("startTime", startTime);
		json.put("endTime", endTime);
		json.put("location", location);
		json.put("description", description);

		StringEntity entity = new StringEntity(json.toString());
		httpPost.setEntity(entity);

		CloseableHttpResponse response = client.execute(httpPost);
		return response.getStatusLine().getStatusCode() == 200;
	}

	public static boolean updateAppointment(long id, String title, String startTime, String endTime, String location,
			String description) throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPut httpPut = new HttpPut(BASE_URL + "/appointments/" + id);
		httpPut.setHeader("Authorization", "Bearer " + jwtToken);
		httpPut.setHeader("Content-type", "application/json");

		JSONObject json = new JSONObject();
		json.put("title", title);
		json.put("startTime", startTime);
		json.put("endTime", endTime);
		json.put("location", location);
		json.put("description", description);

		StringEntity entity = new StringEntity(json.toString());
		httpPut.setEntity(entity);

		CloseableHttpResponse response = client.execute(httpPut);
		return response.getStatusLine().getStatusCode() == 200;
	}

	public static boolean deleteAppointment(long id) throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpDelete httpDelete = new HttpDelete(BASE_URL + "/appointments/" + id);
		httpDelete.setHeader("Authorization", "Bearer " + jwtToken);

		CloseableHttpResponse response = client.execute(httpDelete);
		return response.getStatusLine().getStatusCode() == 200;
	}

	public static JSONArray searchAppointmentsByTitle(String title) throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(BASE_URL + "/appointments/search?title=" + title);
		httpGet.setHeader("Authorization", "Bearer " + jwtToken);

		CloseableHttpResponse response = client.execute(httpGet);
		String responseString = EntityUtils.toString(response.getEntity());
		return new JSONArray(responseString);
	}

	public static JSONArray searchAppointmentsByDate(String date) throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(BASE_URL + "/appointments/search?date=" + date);
		httpGet.setHeader("Authorization", "Bearer " + jwtToken);

		CloseableHttpResponse response = client.execute(httpGet);
		String responseString = EntityUtils.toString(response.getEntity());
		return new JSONArray(responseString);
	}

	public static List<JSONObject> getUpcomingAppointments() throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(BASE_URL + "/appointments/reminders");
		httpGet.setHeader("Authorization", "Bearer " + jwtToken);

		CloseableHttpResponse response = client.execute(httpGet);
		String responseString = EntityUtils.toString(response.getEntity());

		JSONArray jsonArray = new JSONArray(responseString);
		List<JSONObject> result = new ArrayList<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject appt = jsonArray.getJSONObject(i);

			if (!appt.has("reminderSent")) {
				appt.put("reminderSent", false);
			}

			result.add(appt);
		}

		return result;
	}

	public static boolean markAsReminded(int appointmentId) throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(BASE_URL + "/appointments/" + appointmentId + "/reminded");
		httpPost.setHeader("Authorization", "Bearer " + jwtToken);

		CloseableHttpResponse response = client.execute(httpPost);
		return response.getStatusLine().getStatusCode() == 200;
	}

	public static List<String> getLocations() throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(BASE_URL + "/locations");
		httpGet.setHeader("Authorization", "Bearer " + jwtToken);

		CloseableHttpResponse response = client.execute(httpGet);
		String responseString = EntityUtils.toString(response.getEntity()).trim();

		System.out.println("Location API response: " + responseString); // Debug

		if (!responseString.startsWith("[")) {
			throw new JSONException("Expected JSONArray but got: " + responseString);
		}

		JSONArray jsonArray = new JSONArray(responseString);
		List<String> locations = new ArrayList<>();
		for (int i = 0; i < jsonArray.length(); i++) {
			locations.add(jsonArray.getString(i));
		}
		return locations;
	}

	public static boolean addLocation(String location) throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(BASE_URL + "/locations");
		httpPost.setHeader("Authorization", "Bearer " + jwtToken);
		httpPost.setHeader("Content-type", "application/json");

		JSONObject json = new JSONObject();
		json.put("name", location);

		StringEntity entity = new StringEntity(json.toString());
		httpPost.setEntity(entity);

		CloseableHttpResponse response = client.execute(httpPost);
		return response.getStatusLine().getStatusCode() == 201;
	}

	public static boolean updateLocation(String oldLocation, String newLocation) throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPut httpPut = new HttpPut(BASE_URL + "/locations/" + oldLocation);
		httpPut.setHeader("Authorization", "Bearer " + jwtToken);
		httpPut.setHeader("Content-type", "application/json");

		JSONObject json = new JSONObject();
		json.put("name", newLocation);
		StringEntity entity = new StringEntity(json.toString());
		httpPut.setEntity(entity);

		CloseableHttpResponse response = client.execute(httpPut);
		return response.getStatusLine().getStatusCode() == 200;
	}

	public static boolean deleteLocation(String location) throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpDelete httpDelete = new HttpDelete(BASE_URL + "/locations/" + location);
		httpDelete.setHeader("Authorization", "Bearer " + jwtToken);

		CloseableHttpResponse response = client.execute(httpDelete);
		return response.getStatusLine().getStatusCode() == 200;
	}

	public static JSONObject getAppointmentStats() throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(BASE_URL + "/appointments/stats");
		httpGet.setHeader("Authorization", "Bearer " + jwtToken);

		CloseableHttpResponse response = client.execute(httpGet);
		String responseString = EntityUtils.toString(response.getEntity());
		return new JSONObject(responseString);
	}

	public static List<JSONObject> getMissedAppointments() throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(BASE_URL + "/appointments/missed");
		httpGet.setHeader("Authorization", "Bearer " + jwtToken);

		CloseableHttpResponse response = client.execute(httpGet);
		String responseString = EntityUtils.toString(response.getEntity());
		return parseJSONArray(new JSONArray(responseString));
	}

	public static boolean rescheduleAppointment(int appointmentId, String newStartTime, String newEndTime)
			throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();
		String url = BASE_URL + "/appointments/" + appointmentId + "/reschedule?newStartTime=" + newStartTime
				+ "&newEndTime=" + newEndTime;

		HttpPut httpPut = new HttpPut(url);
		httpPut.setHeader("Authorization", "Bearer " + jwtToken);

		CloseableHttpResponse response = client.execute(httpPut);
		return response.getStatusLine().getStatusCode() == 200;
	}

	public static boolean markAsCompleted(long appointmentId) throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(BASE_URL + "/appointments/" + appointmentId + "/complete");
		httpPost.setHeader("Authorization", "Bearer " + jwtToken);

		CloseableHttpResponse response = client.execute(httpPost);
		return response.getStatusLine().getStatusCode() == 200;
	}

	public static boolean sendAppointmentRequest(String receiverUsername, String title, String startTime,
			String endTime, String location, String description) throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(BASE_URL + "/appointment-requests");
		httpPost.setHeader("Authorization", "Bearer " + jwtToken);
		httpPost.setHeader("Content-type", "application/json");

		JSONObject json = new JSONObject();
		json.put("receiverUsername", receiverUsername);
		json.put("title", title);
		json.put("startTime", startTime);
		json.put("endTime", endTime);
		json.put("location", location);
		json.put("description", description);

		StringEntity entity = new StringEntity(json.toString());
		httpPost.setEntity(entity);

		CloseableHttpResponse response = client.execute(httpPost);
		return response.getStatusLine().getStatusCode() == 201;
	}

	public static List<JSONObject> getPendingRequests() throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(BASE_URL + "/appointment-requests/pending");
		httpGet.setHeader("Authorization", "Bearer " + jwtToken);

		CloseableHttpResponse response = client.execute(httpGet);
		String responseString = EntityUtils.toString(response.getEntity());
		return parseJSONArray(new JSONArray(responseString));
	}

	public static boolean respondToAppointmentRequest(int requestId, boolean accept) throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();

		String url = BASE_URL + "/appointment-requests/" + requestId + "/respond?accept=" + accept;
		HttpPut httpPut = new HttpPut(url);
		httpPut.setHeader("Authorization", "Bearer " + jwtToken);

		CloseableHttpResponse response = client.execute(httpPut);
		return response.getStatusLine().getStatusCode() == 200;
	}

	private static List<JSONObject> parseJSONArray(JSONArray array) {
		List<JSONObject> result = new ArrayList<>();
		for (int i = 0; i < array.length(); i++) {
			result.add(array.getJSONObject(i));
		}
		return result;
	}
}