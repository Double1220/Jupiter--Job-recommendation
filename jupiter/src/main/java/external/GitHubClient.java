package external;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import enity.Item;
import enity.Item.ItemBuilder;



public class GitHubClient {
	private static final String URL_TEMPLATE = "https://jobs.github.com/positions.json?description=%s&lat=%s&long=%s";
    private static final String DEFAULT_KEYWORD = "developer";
    
    public List<Item> search(double lat, double lon, String keyword) {
        if (keyword == null) {
            keyword = DEFAULT_KEYWORD;
        }
        try {
            keyword = URLEncoder.encode(keyword, "UTF-8"); //如果用户输入了非法字符会采用UTF-8的编码格式让它变得合理
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = String.format(URL_TEMPLATE, keyword, lat, lon);//把变量整成一个url
        CloseableHttpClient httpClient = HttpClients.createDefault(); //新建一个github client 用来从github上拿资源
        try {
            CloseableHttpResponse response = httpClient.execute(new HttpGet(url)); //从github里拿到的资源返回到response变量里
            if (response.getStatusLine().getStatusCode() != 200) { //如果成功 返回200 这里没有返回200 说明不成功
                return new ArrayList(); //直接返回空
            }
            HttpEntity entity = response.getEntity(); //如果成功返回，但返回结果是空 也返回空
            if (entity == null) {
                return new ArrayList();
            }
            //InputStreamReader 只能一次读一个或者一次读指定多的部分
            //所以就需要用bufferedReader就可以按行读
            BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
            //把读的结果放在responseBody里
            StringBuilder responseBody = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                responseBody.append(line);
            }
            //JSONArray 调用下面的函数转成JSON Item 再返回
            JSONArray array = new JSONArray(responseBody.toString());
            return getItemList(array);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();

    }
    //JSON array to JSON Item
    private List<Item> getItemList(JSONArray array) {
        List<Item> itemList = new ArrayList<>();
        List<String> descriptionList = new ArrayList<>();
        
        for (int i = 0; i < array.length(); i++) {
            // We need to extract keywords from description since GitHub API doesn't return keywords.
            String description = getStringFieldOrEmpty(array.getJSONObject(i), "description");
            //如果description是空就从title拿 因为也有可能description在title里
            if (description.equals("") || description.equals("\n")) {
                descriptionList.add(getStringFieldOrEmpty(array.getJSONObject(i), "title"));
            } else {
                descriptionList.add(description);
            }    
        }

        // We need to get keywords from multiple text in one request since
        // MonkeyLearnAPI has limitations on request per minute.
        // 通过下面这个函数我拿到了每个descrption的key words
        List<List<String>> keywords = MonkeyLearnClient
                .extractKeywords(descriptionList.toArray(new String[descriptionList.size()]));

        for (int i = 0; i < array.length(); ++i) {
            JSONObject object = array.getJSONObject(i);//取出每一个object 
            ItemBuilder builder = new ItemBuilder();
            
            builder.setItemId(getStringFieldOrEmpty(object, "id"));
            builder.setName(getStringFieldOrEmpty(object, "title"));
            builder.setAddress(getStringFieldOrEmpty(object, "location"));
            builder.setUrl(getStringFieldOrEmpty(object, "url"));
            builder.setImageUrl(getStringFieldOrEmpty(object, "company_logo"));
            //dedup 用Hashset
            builder.setKeywords(new HashSet<String>(keywords.get(i)));
            
            Item item = builder.build();
            itemList.add(item);
        }
        
        return itemList;
    }
    
    private String getStringFieldOrEmpty(JSONObject obj, String field) {
        return obj.isNull(field) ? "" : obj.getString(field);
    }


    

}
