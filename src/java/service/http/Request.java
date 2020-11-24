/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package service.http;

import java.util.HashMap;

/**
 *
 * @author SuMario
 */
interface Request {
    HashMap<String, String> request       =new HashMap<String, String>();
    HashMap<String, String> requestCookies=new HashMap<String, String>();
    
    String[] requestHeaders={"HttpRequestMethod","HttpRequestUrl","HttpRequestVersion","Host","User-Agent","Referer"};
    
    StringBuilder posting=new StringBuilder();
    
    public void   setRequest(String key, String value);
    public String getRequest(String key);
}
