package enity;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

public class Item {
	private String itemId;
    private String name;
    private String address;
    private Set<String> keywords;
    private String imageUrl;
    private String url;
    
    private Item(ItemBuilder builder) {
        this.itemId = builder.itemId;
        this.name = builder.name;
        this.address = builder.address;
        this.imageUrl = builder.imageUrl;
        this.url = builder.url;
        this.keywords = builder.keywords;
    }

    
    //变量private函数public: 只能看不能改
	public String getItemId() {
		return itemId;
	}
	public String getName() {
		return name;
	}
	public String getAddress() {
		return address;
	}
	public Set<String> getKeywords() {
		return keywords;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public String getUrl() {
		return url;
	}
	//从gitHub拿到的数据是JSON string,然后我们在githubClient里面变成了JSON array
	//接着在githubClient里，我们把JSON array 变成JSON Item,容易在数据库里面查找， 方便我们自己容易使用
	//然后需要把JSON Item转成JSON object， 在转成JSON string就可以给前端返回了
	public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put("item_id", itemId);
        obj.put("name", name);
        obj.put("address", address);
        obj.put("keywords", new JSONArray(keywords));
        obj.put("image_url", imageUrl);
        obj.put("url", url);
        return obj;
    }
	//ItemBuilder 解决constructor的问题
	//下面的setter如果返回ItemBuilder 就可以连着call chaining
	//static 就是说属于这个类而不是属于这个instance，如果没有static, 就需要先new 一个Item

	public static class ItemBuilder {
        private String itemId;
        private String name;
        private String address;
        private String imageUrl;
        private String url;
        private Set<String> keywords;
        
        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setKeywords(Set<String> keywords) {
            this.keywords = keywords;
        }
        public Item build() {
            return new Item(this);
        }

      }
    
}
