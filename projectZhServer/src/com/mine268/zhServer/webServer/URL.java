package com.mine268.zhServer.webServer;

public class URL {
    public String scheme = null;
    public String host = null;
    public String port = "80";
    public String path = null;
    //public String parameters = null;
    public String query = null;
    public String fragment = null;

    public URL(String in_url){
        String t_url = in_url;
        //1.
        if(t_url.contains("://")){
            this.scheme = in_url.split("://")[0];
            t_url = in_url.split("://")[1];
        }
        //2.
        if(t_url.contains("#")){
            this.fragment = t_url.split("#")[1];
            t_url = t_url.split("#")[0];
        }
        //3.
        if(t_url.contains("?")){
            this.query = t_url.split("/?")[1];
            t_url = t_url.split("/?")[0];
        }
        //4.
        if(t_url.contains("/")){
            this.path = t_url.substring(t_url.indexOf("/"));
            if(this.path.length() == 1) this.path = WebServerConfig.default_page_path;
            //t_url = t_url.split("/")[0];
        }
        /*else{
            this.path = WebServerConfig.default_page_path;
        }*/
        //5.
        if(t_url.contains(":")){
            this.port = t_url.split(":")[1];
            t_url = t_url.split(":")[0];
        }
        //6.
        //this.host = t_url;
    }
}
