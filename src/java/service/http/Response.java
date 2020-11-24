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
interface Response {
    HashMap<String, String> response       =new HashMap<String, String>();
    HashMap<String,String>  responseCookies=new HashMap<String, String>();
    
    String[] responseHeaders={"HttpResponseVersion","HttpResponseCode","HttpResponseDescription"
            + "Date","Server","Location","Content-Length","Content-Type"};
    
    public void   setResponse(String key, String value);
    public String getResponse(String key);
}
