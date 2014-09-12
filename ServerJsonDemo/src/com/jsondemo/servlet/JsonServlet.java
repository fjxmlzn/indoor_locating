package com.jsondemo.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jsondemo.servlet.JsonFromDatabase;
import net.sf.json.JSONObject;

public class JsonServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of the object.
	 */
	public JsonServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); 
		// Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		JsonFromDatabase jsondao = new JsonFromDatabase();
		response.setContentType("text/html;charset=UTF-8");
		//首先获取输入json数据流中的type信息
		String type = request.getParameter("type").toString().trim();
		//如果输入数据的type为"search"，则执行查询数据库功能
		if(type.equals("search")){
			String name = request.getParameter("name").toString().trim();
			String str = null;
			PrintWriter out = response.getWriter();
			//查询 mySQL数据库，返回name用户的信息
			str = jsondao.getUserInfo(name);
			System.out.println("json"+str);
			if(str.equals("noItem")){
				out.print("noItem");
				System.out.println("noItem");
				out.flush();
			}else if(str.equals("pwdError")){
				out.print("pwdError");
				System.out.println("pwdError");
				out.flush();
			}else{
				//封装由服务器返回的JSON对象
				JSONObject jsonReply = JSONObject.fromObject(str);
				//将封装的json数据流写入输出流
				out.write(jsonReply.toString());
				System.out.println(jsonReply);
			}
			out.flush();
			out.close();
		}
		//如果type为"input"，则执行向mySQL数据库写入数据功能
		else if(type.equals("input")){
			//获取request中的用户name, age, sex信息
			String name = request.getParameter("name").toString().trim();
			String age = request.getParameter("age").toString().trim();
			String sex = request.getParameter("sex").toString().trim();
            String str = "Name: " +name+" Age: "+age+" Sex "+sex;
            System.out.println(str);
            //封装上述信息成为一个json对象
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", name);
            jsonObject.put("age", age);
            jsonObject.put("sex", sex);
            //连接mySQL数据库，写入此Json对象
            JsonFromDatabase inputit = new JsonFromDatabase();
            inputit.InsertJsonIntoDatabase(jsonObject);
		}
		
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
