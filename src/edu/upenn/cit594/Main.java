package edu.upenn.cit594;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;

import edu.upenn.cit594.datamanagement.*;
import edu.upenn.cit594.logging.Logger;
import edu.upenn.cit594.util.*;
import edu.upenn.cit594.processor.*;
import edu.upenn.cit594.ui.UserInterface;

public class Main {
	// Declare static variables
	public static String populationFilename = "population";
	public static String propertyFilename = "properties";
	public static String covidFilename = "covid";
	
	// covid, population
	public static void main(String[] args) throws IOException {
		
		// Declare variables first
		List<PropertyData> propertyData = new ArrayList<>();
		List<PopulationData> populationData = new ArrayList<>();
		List<CovidData> covidData = new ArrayList<>();
		
		//Logger
		Logger logger = Logger.getInstance();
				
		String filePattern = "^--(?<name>.+?)=(?<value>.+)$";
		Pattern pattern = Pattern.compile(filePattern);
		
		try {
			for (String arg: args) {
				Matcher matcher = pattern.matcher(arg);
				if (matcher.matches()) {
					String name = matcher.group("name");
					if(name.equals("population")){
						String populationFilePath = matcher.group("value");
						PopulationDataReader populationReader = new PopulationDataReader(populationFilePath);
						populationData = populationReader.readPopulationData();
					}
					else if(name.equals("properties")){
						String propertyFilePath = matcher.group("value");
						PropertyDataReader propertyReader = new PropertyDataReader(propertyFilePath);
			            propertyData = propertyReader.readPropertyData();
					}
					else if(name.equals("covid")){
						String covidFilePath = matcher.group("value");
						CovidDataReader covidReader;
			            if (covidFilePath.endsWith(".csv")) {
			                covidReader = new CovidCSVReader(covidFilePath);
			            } else if (covidFilePath.endsWith(".json")) {
			                covidReader = new CovidJSONReader(covidFilePath);
			            } else {
			                throw new IllegalArgumentException("Unsupported file format: " + covidFilePath);
			            }
			            covidData = covidReader.readCovidData();
					}
					else if(name.equals("log")) {
						String destinationFile = matcher.group("value");
						logger.setDestination(destinationFile);
					}
				}
			}
				
				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		String firstLine = "";
		for (int i = 0; i < args.length; i++) {
			firstLine = firstLine + " " + args[i];
		}
		String currentTime = String.valueOf(System.currentTimeMillis());
		String event = currentTime + " " + firstLine;
		logger.logEvent(event);
		
		DataProcessor dataProcessor = new DataProcessor(covidData, propertyData, populationData);
		UserInterface ui = new UserInterface();
		ui.displayMenu();
		Scanner scanner = new Scanner(System.in);
		int option = scanner.nextInt();
		currentTime = String.valueOf(System.currentTimeMillis());
		event = currentTime + " " + option;
		logger.logEvent(event);
		
		while(true) {
			if (option == 0) {
				System.out.println("Exiting the program");
				return;
			}
	//		else if (option == 1) {
	//			if (args[0])
	//			System.out.println();
	//		}
			else if (option == 2) { 
				currentTime = String.valueOf(System.currentTimeMillis());
				event = currentTime + " " + populationFilename;
				logger.logEvent(event);
				int totalPopulation = dataProcessor.getTotalPopulation();
				System.out.println("BEGIN OUTPUT" + "\n" + totalPopulation + "\n" + "END OUTPUT");
				System.out.println("Total Population: " + totalPopulation + "\n");
				ui.displayMenu();
				option = scanner.nextInt();
				currentTime = String.valueOf(System.currentTimeMillis());
				event = currentTime + " " + option;
				logger.logEvent(event);
				
			}
			else if (option == 3) {
				System.out.println("Do you want 'partial' or 'full' vaccinations?");
				String typeOfVaccination = scanner.next().toLowerCase();
				currentTime = String.valueOf(System.currentTimeMillis());
				event = currentTime + " " + typeOfVaccination;
				logger.logEvent(event);
				
				while (!typeOfVaccination.equals("partial") || !typeOfVaccination.equals("full")) {
					if (typeOfVaccination.equals("partial") || typeOfVaccination.equals("full")) {
				        break;
				       }
					System.out.println("That's not a valid option, try again: \n");
					typeOfVaccination = scanner.next().toLowerCase();
					currentTime = String.valueOf(System.currentTimeMillis());
					event = currentTime + " " + typeOfVaccination;
					logger.logEvent(event);
				} 
				
				System.out.println("Can you please enter a date in the format (YYYY-MM-DD): ");
				String date = scanner.next();
				Pattern regex = Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$");
				Matcher matcher = regex.matcher(date);
				while (!matcher.matches()) {
					if (matcher.matches()) {
				        break;
				       }
					System.out.println("That's not a valid option, try again, please enter a date in the format (YYYY-MM-DD): ");
					date = scanner.next();
					currentTime = String.valueOf(System.currentTimeMillis());
					event = currentTime + " " + date;
					logger.logEvent(event);
				}
				currentTime = String.valueOf(System.currentTimeMillis());
				event = currentTime + " " + covidFilename + " " + populationFilename;
				logger.logEvent(event);
//				vacPerCapita(date, typeOfVaccination, covidData, populationData);
				System.out.println("Get Vaccinations"); // delete later
				ui.displayMenu();
				option = scanner.nextInt();
				currentTime = String.valueOf(System.currentTimeMillis());
				event = currentTime + " " + option;
				logger.logEvent(event);
			}
			else if (option == 4) {
				System.out.println("Can you please enter a 5 digit zip code: ");
				String zipCode = scanner.next();
				currentTime = String.valueOf(System.currentTimeMillis());
				event = currentTime + " " + zipCode;
				logger.logEvent(event);
				while (!zipCode.matches("\\d{5}")) {
					if (zipCode.matches("\\d{5}")) {
						break;
					}
					System.out.println("That's not a valid option, try again, please enter a 5 digit zip code: ");
					zipCode = scanner.next();
					currentTime = String.valueOf(System.currentTimeMillis());
					event = currentTime + " " + zipCode;
					logger.logEvent(event);
				}
				currentTime = String.valueOf(System.currentTimeMillis());
				event = currentTime + " " + propertyFilename + " " + populationFilename;
				logger.logEvent(event);
				double averageMarketValue = dataProcessor.getAverageMarketValue(zipCode);
	            System.out.println("Average Market Value for " + zipCode + ": " + averageMarketValue);
	            ui.displayMenu();
				option = scanner.nextInt();
				currentTime = String.valueOf(System.currentTimeMillis());
				event = currentTime + " " + option;
				logger.logEvent(event);
			}
			else if (option == 5) {
				System.out.println("Can you please enter a 5 digit zip code: ");
				String zipCode = scanner.next();
				currentTime = String.valueOf(System.currentTimeMillis());
				event = currentTime + " " + zipCode;
				logger.logEvent(event);
				while (!zipCode.matches("\\d{5}")) {
					if (zipCode.matches("\\d{5}")) {
						break;
					}
					System.out.println("That's not a valid option, try again, please enter a 5 digit zip code: ");
					zipCode = scanner.next();
					currentTime = String.valueOf(System.currentTimeMillis());
					event = currentTime + " " + zipCode;
					logger.logEvent(event);
				}
				currentTime = String.valueOf(System.currentTimeMillis());
				event = currentTime + " " + propertyFilename;
				logger.logEvent(event);
				double averageLivableArea = dataProcessor.getAverageLivableArea(zipCode);
	            System.out.println("Average Livable Area for " + zipCode + ": " + averageLivableArea);
	            ui.displayMenu();
				option = scanner.nextInt();
				currentTime = String.valueOf(System.currentTimeMillis());
				event = currentTime + " " + option;
				logger.logEvent(event);
			}
			else if (option == 6) {
				System.out.println("Can you please enter a 5 digit zip code: ");
				String zipCode = scanner.next();
				currentTime = String.valueOf(System.currentTimeMillis());
				event = currentTime + " " + zipCode;
				logger.logEvent(event);
				while (!zipCode.matches("\\d{5}")) {
					if (zipCode.matches("\\d{5}")) {
						break;
					}
					System.out.println("That's not a valid option, try again, please enter a 5 digit zip code: ");
					zipCode = scanner.next();
					currentTime = String.valueOf(System.currentTimeMillis());
					event = currentTime + " " + zipCode;
					logger.logEvent(event);
				}
				currentTime = String.valueOf(System.currentTimeMillis());
				event = currentTime + " " + propertyFilename + " " + populationFilename;
				logger.logEvent(event);
				int totalMarketValuePerCapita = dataProcessor.getTotalMarketValuePerCapita(zipCode);
	            System.out.println("Total Market Value Per Capita for " + zipCode + ": " + totalMarketValuePerCapita);
	            ui.displayMenu();
				option = scanner.nextInt();
				currentTime = String.valueOf(System.currentTimeMillis());
				event = currentTime + " " + option;
				logger.logEvent(event);
			}
			else if (option == 7) {
				System.out.println("Can you please enter a 5 digit zip code: ");
				String zipCode = scanner.next();
				currentTime = String.valueOf(System.currentTimeMillis());
				event = currentTime + " " + zipCode;
				logger.logEvent(event);
				while (!zipCode.matches("\\d{5}")) {
					if (zipCode.matches("\\d{5}")) {
						break;
					}
					System.out.println("That's not a valid option, try again, please enter a 5 digit zip code: ");
					zipCode = scanner.next();
					currentTime = String.valueOf(System.currentTimeMillis());
					event = currentTime + " " + zipCode;
					logger.logEvent(event);
				}
				currentTime = String.valueOf(System.currentTimeMillis());
				event = currentTime + " " + propertyFilename + " " + populationFilename + " " + covidFilename;
				logger.logEvent(event);
//				int correlation = getCorrelation(zipCode);
				System.out.println("Get Correlation");
	            ui.displayMenu();
				option = scanner.nextInt();
				currentTime = String.valueOf(System.currentTimeMillis());
				event = currentTime + " " + option;
				logger.logEvent(event);
			}
			else {
				System.out.println("That's not a valid option, try again: \n");
				ui.displayMenu();
				option = scanner.nextInt();
				currentTime = String.valueOf(System.currentTimeMillis());
				event = currentTime + " " + option;
				logger.logEvent(event);
			} 
		}  
		
	}

}