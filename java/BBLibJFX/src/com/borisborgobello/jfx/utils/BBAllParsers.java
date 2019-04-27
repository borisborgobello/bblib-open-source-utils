package com.borisborgobello.jfx.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;



public class BBAllParsers {
	/**
	 * Parsing String to XML (Document)
	 * 
	 * @param rawXML
	 * @return
	 */
	/*
	protected Document parseStringToXML(String rawXML){
		
		Document document = null;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
        try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			InputSource is = new InputSource();
	        is.setCharacterStream(new StringReader(rawXML));
	        document = db.parse(is); 

		} catch (ParserConfigurationException e) {
			e.printStackTrace(); // XML parse error
			return null;
		} catch (SAXException e) {
			e.printStackTrace(); // Wrong XML file structure
            return null;
		} catch (IOException e) {
			e.printStackTrace(); // I/O exeption
			return null;
		}

        return document;
	}
	
	public static final JSONArray parseInputStreamToJSONArray(InputStream is) {
		String jsonStr = DVFileInout.inputStreamToString2(is);
		JSONArray json = parseStringToJSONArray(jsonStr);
		try { is.close(); } catch (Exception e) {}
		return json;
	}
	
	public static final JSONObject parseInputStreamToJSON(InputStream is) {
		String jsonStr = DVFileInout.inputStreamToString2(is);
		JSONObject json = parseStringToJSON(jsonStr);
		try { is.close(); } catch (Exception e) {}
		return json;
	}
	
	
	public static final JSONObject parseStringToJSON(String rawJSON){
		
		JSONObject object = null;
		
		if( rawJSON == null ) return null;
		
		if( rawJSON.length() <= 2 ) return null;
		
		try {
			object = new JSONObject( rawJSON );
		} catch (JSONException e) {
			DVLog.w("JSON", e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		return object;
	}
	
	public static final JSONArray parseStringToJSONArray(String rawJSON){
		
		JSONArray object = null;
		
		if( rawJSON == null ) return null;
		
		if( rawJSON.length() <= 2 ) return null;
		
		try {
			object = new JSONArray( rawJSON );
		} catch (JSONException e) {
			e.printStackTrace();
			DVLog.w("JSON", e.getLocalizedMessage());
		}
		
		return object;
	}
	
	public static final HashMap<String, String> convertPureStringStringJSONObjectToMap(JSONObject jsonObject) {
		HashMap<String, String> jsonMap = new HashMap<String, String>();
		
		@SuppressWarnings("unchecked")
		Iterator<String> it = jsonObject.keys();
		
		while (it.hasNext()) {
			String key = it.next();
			String value = null;
			try {
				value = jsonObject.getString(key);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			jsonMap.put(key, value);
		}
		return jsonMap;
	}
	
	
	public static final HashMap<String, Object> convertStringToObjectJSONObjectToMap(JSONObject jsonObject) {
		HashMap<String, Object> jsonMap = new HashMap<String, Object>();
		
		@SuppressWarnings("unchecked")
		Iterator<String> it = jsonObject.keys();
		
		while (it.hasNext()) {
			String key = it.next();
			Object value = null;
			try {
				value = jsonObject.get(key);
				if (value instanceof JSONArray) {
					value = convertPureStringStringJSONArrayToArray((JSONArray) value);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			jsonMap.put(key, value);
		}
		return jsonMap;
	}
	
	public static final ArrayList<String> convertPureStringStringJSONArrayToArray(JSONArray array) {
		ArrayList<String> pureStringArray = new ArrayList<String>();
		
		for (int i = 0; i < array.length(); i++) {
			try {
				pureStringArray.add(array.getString(i));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return pureStringArray;
	}
	
	
	/**
	 * Used to recieve WebServices response
	 * 
	 * @param urlString
	 * 		URL without query string at the end
	 * @return
	 */
	
	/*
	protected String getResponse(String urlString, QueryString request) {
        
		DVVSLog.i("WebServices.getResponse()", "Enter");
		
		String response = "";
		
		// Adding query string part to URL
		
		if(request instanceof QueryString){
			urlString = urlString + request.buildGetQueryString();
		}

    	DVVSLog.d( "WebServices.getResponse().urlString", urlString );
    	DVVSLog.i( "WebServices.getResponse()", "Entering critical section");

		// Send request
		response = getHttpResponse(urlString );
		
		DVVSLog.i("WebServices.getResponse()", "Exit");
		
    	return response;
	}
	
	public static String getHttpResponse(String queryUrl) {
		
        String         response       = null;
        StringBuffer   stringBuffer   = new StringBuffer();
        BufferedReader bufferedReader = null;
        
        try {
        	
            // prepare getRequest
            
            HttpGet httpGet = new HttpGet();
            URI uri = new URI( queryUrl );
            httpGet.setURI( uri );
            
            
            // Create connection
            
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute( httpGet );
            
            
            // getResponse
            
            InputStream inputStream = httpResponse.getEntity().getContent();
            
            DVVSLog.e( "WebServices", "Getting BufferedReader" );
            
            bufferedReader = new BufferedReader( 
                    new InputStreamReader( inputStream ), 1024 );
            
            String readLine = bufferedReader.readLine();
            
            while ( readLine != null ) {
                
                stringBuffer.append( readLine );
                
                readLine = bufferedReader.readLine();
            }

            
        } catch ( OutOfMemoryError e ){
            
            e.printStackTrace();
            
            System.gc();
            
            return null;
            
        } catch (Exception e) {
        	DVVSLog.e("WebServices", "IOException trying to execute request for " + e);
            return null;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    return null;
                }
            }
            response = stringBuffer.toString();
           	DVVSLog.i("WebServices", "Reponse = " + response);

        }

        return response;
    }*/
    
        private static final String A = "vietnamVIETNAMvietnam77";
        
        public static final <T> boolean savePOJOToFile(File f, final Object o, boolean crypted) throws IOException {
            ObjectMapper mapper = new ObjectMapper();
            if (!crypted) {
                mapper.writeValue(f, o);
                return true;
            }
            
            byte[] data = mapper.writeValueAsBytes(o);
            data = BBCipherTools.cryptAES128(BBCipherTools.getFullkeyAES128FromPartialKey(A), data);
            Files.write(f.toPath(), data, StandardOpenOption.CREATE);
            return true;
        } 
        
        public static final String getValuePOJOasString(final Object o) throws IOException {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(o);
        }
        
        
        public static final byte[] getValuePOJOasBytes(final Object o, boolean crypted) throws IOException {
            ObjectMapper mapper = new ObjectMapper();
            byte[] data = mapper.writeValueAsBytes(o);
            if (!crypted) {
                return data;
            }
            data = BBCipherTools.cryptAES128(BBCipherTools.getFullkeyAES128FromPartialKey(A), data);
            return data;
        }
        
        /*public static final <T> File savePOJOToZippedFile(File f, final Object o) throws IOException {
            ObjectMapper mapper = new ObjectMapper();
            
            String dataS = mapper.writeValueAsString(o);
            //data = BBCipherTools.cryptAES128(BBCipherTools.getFullkeyAES128FromPartialKey(A), data);
            
            //Files.write(f.toPath(), data, StandardOpenOption.CREATE);
            
            File zipFile = new File(f.getPath()+".zip");
            
            new BBZipper(zipFile)
                    .start()
                    .addEntry(ISTools.extractFilename(f.getPath(), true), dataS.getBytes())
                    .finish();
            
            return zipFile;
        } */
    
        
        public static final <T> T getPOJOFromBytes(byte[] data, final Class<T> clazz, boolean crypted) 
			throws Exception {
            T so = null;
            ObjectMapper om = new ObjectMapper();
            if (!crypted) {
		 so = om.readValue(data, clazz);
		return so;
            }
            
            data = BBCipherTools.decryptAES128(BBCipherTools.getFullkeyAES128FromPartialKey(A), data);
            so = om.readValue(data, clazz);
            return so;
	}
    
        public static final <T> T getPOJOFromJsonFile(File f, final Class<T> clazz, boolean crypted) 
			throws Exception {
            T so = null;
            ObjectMapper om = new ObjectMapper();
            if (!crypted) {
		 so = om.readValue(f, clazz);
		return so;
            }
            
            byte[] data = Files.readAllBytes(f.toPath());
            data = BBCipherTools.decryptAES128(BBCipherTools.getFullkeyAES128FromPartialKey(A), data);
            so = om.readValue(data, clazz);
            return so;
	}
        
        public static final HashMap<String,Object> getHashMapFromJsonFile(File f, boolean crypted) 
			throws IOException {
            byte[] data = Files.readAllBytes(f.toPath());
            if (crypted) data = BBCipherTools.decryptAES128(BBCipherTools.getFullkeyAES128FromPartialKey(A), data);
            
            ObjectMapper mapper = new ObjectMapper();
            return new HashMap<>(mapper.readValue(data, new TypeReference<Map<String, Object>>(){}));
	}
        
        //map = 
        
	
	/*public static final <T> T getPOJOFromJson(byte[] jsonUtf8, final Class<T> clazz) 
			throws Exception {
		if (jsonUtf8 == null) return null;
		String json = new String(jsonUtf8, "UTF-8");
		//DVLog.v(TAG, json);
		ObjectMapper om = new ObjectMapper();
		T so = om.readValue(json, clazz);
		return so;
	}*/
	
	/*public static final <T> T getPOJOFromJson(JSONObject object, final Class<T> clazz) 
			throws Exception {
		//DVLog.v(TAG, object.toString(2));
		ObjectMapper om = new ObjectMapper();
		T so = om.readValue(object.toString(), clazz);
		return so;
	}*/
        
        
}
