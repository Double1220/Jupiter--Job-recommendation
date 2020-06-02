package rpc;

import java.io.IOException;
import external.GitHubClient;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.PrintWriter;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import enity.Item;

/**
 * Servlet implementation class SearchItem
 */
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//如果当前没有这个session 则403
		HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(403);
            return;
        }
        
        String userId = request.getParameter("user_id");
		double lat = Double.parseDouble(request.getParameter("lat"));
        double lon = Double.parseDouble(request.getParameter("lon"));

        GitHubClient client = new GitHubClient(); //create a new GitHubClient object 在 external包里的GitHubClient文件
        //把lat lon变量的值传入
        //拿到从github返回的结果
        //再返回给前端先把JSON item转成JSON object, 再转成JSON string的格式传给前端
        List<Item> items = client.search(lat, lon, null);
        JSONArray array = new JSONArray();
        for (Item item : items) {
            array.put(item.toJSONObject());
        }
        //writeJsonArray里面会有toString的函数
        RpcHelper.writeJsonArray(response, array);

  
        
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
