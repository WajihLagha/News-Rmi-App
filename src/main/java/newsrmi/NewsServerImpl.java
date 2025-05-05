package newsrmi;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.*;
import java.io.*;
import java.net.*;
import org.json.*;

public class NewsServerImpl extends UnicastRemoteObject implements NewsInterface {
    private static final String API_KEY = "ffa77e76a2404a1d9951bd4410891f52";

    protected NewsServerImpl() throws RemoteException {
        super();
    }

    @Override
    public List<String> getNewsHeadlines(String topic) throws RemoteException {
        List<String> headlines = new ArrayList<>();
        try {
            String apiUrl = "https://newsapi.org/v2/everything?q="
                    + URLEncoder.encode(topic, "UTF-8")
                    + "&sortBy=publishedAt"
                    + "&pageSize=5"
                    + "&apiKey=" + API_KEY;

            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) sb.append(line);
            in.close();

            System.out.println("🟡 Raw JSON Response:");
            System.out.println(sb.toString());

            JSONObject root = new JSONObject(sb.toString());

            if (root.has("status") && root.getString("status").equals("error")) {
                headlines.add("API error: " + root.getString("message"));
                return headlines;
            }

            JSONArray articles = root.getJSONArray("articles");
            for (int i = 0; i < Math.min(5, articles.length()); i++) {
                headlines.add(articles.getJSONObject(i).getString("title"));
            }

            if (headlines.isEmpty()) {
                headlines.add("⚠️ No news found for topic: " + topic);
            }
        } catch (Exception e) {
            e.printStackTrace();
            headlines.add("Error fetching news.");
        }
        return headlines;
    }

}
