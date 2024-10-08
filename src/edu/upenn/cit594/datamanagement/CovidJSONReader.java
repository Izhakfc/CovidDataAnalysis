package edu.upenn.cit594.datamanagement;

import java.io.File;

import java.io.FileReader;
import java.io.IOException;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.upenn.cit594.util.CovidData;


public class CovidJSONReader {

	
	public CovidJSONReader() {
		// TODO Auto-generated constructor stub
	}

	public List<CovidData> readCovidDataJson(String filename){
		List<CovidData> covidData = new ArrayList<>();
		File file = new File(filename);
		FileReader fr;
		Pattern regex = Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])\\s([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$");
		Matcher matcher;
		String zipCode;
		String dateTime;
		
		try {
			fr = new FileReader(file);
			JSONArray jsonArray = (JSONArray) new JSONParser().parse(fr);
			Iterator<?> iterator = jsonArray.iterator();
			while(iterator.hasNext()) {
				JSONObject line  = (JSONObject) iterator.next();
				if ((String.valueOf(line.get("zip_code")).length() != 5)) {
					continue;
				}

				zipCode = line.get("zip_code").toString();
	
				Object partiallyVaccinatedObj = line.get("partially_vaccinated");
				
				int partiallyVaccinated = parseIntOrZero(partiallyVaccinatedObj);
                
				
				Object fullyVaccinatedObj = line.get("fully_vaccinated");
				int fullyVaccinated = parseIntOrZero(fullyVaccinatedObj);
			
		
				if (line.get("etl_timestamp") == null || line.get("etl_timestamp") == "") {
					continue;
				}
				
				else {
					matcher = regex.matcher((CharSequence) line.get("etl_timestamp"));
					if (matcher.matches()) {
						dateTime = (String) line.get("etl_timestamp");
						CovidData covidLine = new CovidData(zipCode, partiallyVaccinated, fullyVaccinated, dateTime);
						covidData.add(covidLine);
					}
					else {
					System.out.println("This line is being skipped because it does not have a valid date/time format.");
					continue;
				}	
			}
			}	
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return covidData;
	}
	 private int parseIntOrZero(Object value) {
	        if (value instanceof Long) {
	            return ((Long) value).intValue();
	        } else if (value instanceof String) {
	            try {
	                return Integer.parseInt((String) value);
	            } catch (NumberFormatException e) {
	                return 0;
	            }
	        }
	        return 0;
	    }
}
