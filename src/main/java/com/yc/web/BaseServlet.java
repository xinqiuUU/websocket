package com.yc.web;

import com.google.gson.Gson;
import com.yc.utils.JsonModel;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

public abstract class BaseServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String op = req.getParameter("op"); //op=regFile
        JsonModel jm = new JsonModel(); //用来保存要运行后的信息  并  返回到前端
        try {
            if (op == null || "".equals(op)) {
                // out.println( "{code:0,error:'没有op参数'}"  );
                jm.setCode(0);
                jm.setError("op参数不能为空..");
                writeJson(jm,resp);
                return;
            }
            ///        反 射
            Method[] methods  = this.getClass().getDeclaredMethods();//取子类中的方法
            for (Method m:methods){
                if (  m.getName().equals(  op  )  ) {  // 判断有没有 regFile方法
                    m.invoke(this, req,  resp);//激活对应函数  regFile
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            jm.setCode(0);
            jm.setError(  e.getMessage()  );
            writeJson(jm,resp);
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");  // 响应流的编码
        resp.setContentType("text/html;charset=utf-8");

        super.service(req, resp);
    }

    //*** 后端传数据到前端  关键地方   ***    以json格式传数据到前端
    protected void writeJson(  JsonModel jm , HttpServletResponse resp) throws IOException {
        resp.setContentType("text/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        Gson g = new Gson();
        out.println(  g.toJson(  jm  )); ///后端 把 运行情况 以json类型传出到前端
        out.flush();
        out.close();
    }
    protected void writeObj(Object obj, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/json;charset=utf-8");
        PrintWriter out =resp.getWriter();
        //创建一个Gson对象 g，
        Gson g = new Gson();
//      用于将Java对象转换为JSON格式的字符串
        out.print(g.toJson(     obj      ));
        out.flush();
        out.close();
    }

}
