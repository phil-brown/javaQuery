/*
 * Copyright 2013 Phil Brown
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package self.philbrown.javaQuery;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Handle an HttpResponse as a {@link JSONObject} or {@link JSONArray}. 
 * @author Phil Brown
 */
public class JSONResponseHandler implements ResponseHandler<Object> 
{
	
	@Override
	public Object handleResponse(HttpResponse response) throws ClientProtocolException, IOException 
	{
		StatusLine statusLine = response.getStatusLine();
		if (statusLine.getStatusCode() >= 300)
        {
        	Log.e("javaQuery", "HTTP Response Error " + statusLine.getStatusCode() + ":" + statusLine.getReasonPhrase());
        	return null;
        }

        HttpEntity entity = response.getEntity();
        if (entity == null) 
        	return null;
        
        String json = null;
        try 
        {
        	json = EntityUtils.toString(entity);
        	if (json.startsWith("{"))
        	{
        		return new JSONObject(json);
        	}
        	else
        	{
        		return new JSONArray(json);
        	}
        	
		} 
        catch (ParseException e) 
        {
			throw e;
		} 
        catch (JSONException e) 
        {
        	throw new IOException("Received malformed JSON");
        	
		}
	}

}
