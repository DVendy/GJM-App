package com.example.adi.gjm_app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by DVendy - Kreasys on 5/28/2015.
 */
public class ServerDatabaseHandler {
    private String baseUrl;
    final String arrJSONKey = "gjm";

    public ServerDatabaseHandler(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getJSONUrl(String url) {
        StringBuilder str = new StringBuilder();
        try {
            URL url2 = new URL(url);
            str = new StringBuilder();

            //HttpResponse response = client.execute(httpGet);
            HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);

            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                str.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return str.toString();
    }

    public String getJSONUrl(String url, String param) {
        StringBuilder str = new StringBuilder();
        OutputStreamWriter request;
        try {
            URL url2 = new URL(url);

            //HttpResponse response = client.execute(httpGet);
            HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);

            request = new OutputStreamWriter(conn.getOutputStream());
            request.write(param);
            request.flush();
            request.close();

            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                str.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch(Exception e){
            e.printStackTrace();
        }
        //System.out.println(str.toString());
        return str.toString();
    }

    public void addHistory(String date, int user_id) throws JSONException {
        String param = "date="+date+"&user_id="+user_id;
        String jsonString = getJSONUrl(baseUrl + "/TesDatabase/login_history.php",param);
    }

    public User getUser(String username) throws JSONException {
        String jsonString = getJSONUrl(baseUrl + "/TesDatabase/login.php");

        if(jsonString!=null) {
            JSONObject data = new JSONObject(jsonString);
            JSONArray Jarray = data.getJSONArray(arrJSONKey);

            List<User> users = new ArrayList<User>();
            for (int i = 0; i < Jarray.length(); i++) {
                JSONObject c = Jarray.getJSONObject(i);
                if (username.equals(c.getString("username")))
                    return new User(Integer.parseInt(c.getString("id")), c.getString("username"), c.getString("md5"), c.getString("name"), c.getString("email"), c.getString("hp"));
            }
            return null;
        }
        else
        {
            return null;
        }
    }

    public int updateUser(User newUser) throws JSONException {
        String param = "id="+newUser.getId()+"&name="+newUser.getName()+"&md5="+newUser.getPassword();
        String jsonString = getJSONUrl(baseUrl + "/TesDatabase/user_update.php",param);

        if(jsonString==null)//failed
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }

    public String getVersion() throws JSONException {
        String jsonString = getJSONUrl(baseUrl + "/TesDatabase/version.php");

        if(jsonString!=null) {
            JSONObject data = new JSONObject(jsonString);
            JSONArray Jarray = data.getJSONArray(arrJSONKey);

            String version = "";
            for (int i = 0; i < Jarray.length(); i++) {
                JSONObject c = Jarray.getJSONObject(i);
                version = c.getString("date");
            }
            return version;
        }
        else
        {
            return null;
        }
    }

    public List<Product> getProducts(String version, int limit, int offset) throws JSONException {
        String param = "version="+version+"&offset="+offset+"&limit="+limit;
        String jsonString = getJSONUrl(baseUrl + "/TesDatabase/product.php",param);

        if(jsonString!=null) {
            JSONObject data = new JSONObject(jsonString);
            JSONArray productArray = data.getJSONArray(arrJSONKey);

            List<Product> products = new ArrayList<Product>();
            for (int i = 0; i < productArray.length(); i++) {
                JSONObject c = productArray.getJSONObject(i);
                products.add(new Product(Integer.parseInt(c.getString("id")),
                        c.getString("itemcode"),
                        c.getString("itemname"),
                        c.getString("name"),
                        c.getString("merek"),
                        c.getString("model"),
                        c.getString("spec"),
                        c.getString("registrasi"),
                        c.getString("kurs"),
                        Double.parseDouble(c.getString("price")),
                        c.getString("lastupdate")));
            }

            return products;
        }
        else
        {
            return null;
        }
    }

    public int getProductCount(String version) throws JSONException {
        String param = "version="+version;
        String jsonString = getJSONUrl(baseUrl + "/TesDatabase/count.php",param);

        if(jsonString!=null) {
            JSONObject data = new JSONObject(jsonString);
            JSONArray Jarray = data.getJSONArray(arrJSONKey);
            int count = 0;
            for (int i = 0; i < Jarray.length(); i++) {
                JSONObject c = Jarray.getJSONObject(i);
                count = Integer.parseInt(c.getString("COUNT(*)"));
            }
            return count;
        }
        else
        {
            return 0;
        }
    }

    public int checkForUpdate(String dbVersion)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date version_device, version_server;
        try {
            version_device = dateFormat.parse(dbVersion);
            version_server = dateFormat.parse(getVersion());
            //System.out.println("===============================================ini VERSI hp : " + version_device);
            //System.out.println("===============================================ini VERSI server : " + version_server);

            if (version_device.getTime() != version_server.getTime())
                return 0; //need update
            else
                return 2; //up to date
        } catch(JSONException e) {
            return 0; //an error means need update
        } catch (ParseException ignored) {
            return 0; //an error means need update
        }
    }

    public List<News> getNews(int lastNewsId) throws JSONException {
        String param = "id="+lastNewsId;
        String jsonString = getJSONUrl(baseUrl + "/TesDatabase/news.php",param);

        if(jsonString!=null) {
            JSONObject data = new JSONObject(jsonString);
            JSONArray productArray = data.getJSONArray(arrJSONKey);

            List<News> news = new ArrayList<News>();
            for (int i = 0; i < productArray.length(); i++) {
                JSONObject c = productArray.getJSONObject(i);
                news.add(new News(Integer.parseInt(c.getString("id")),
                        c.getString("title"),
                        c.getString("body"),
                        c.getString("date")));
            }

            return news;
        }
        else
        {
            return null;
        }
    }

}
