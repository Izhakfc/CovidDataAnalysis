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
    
    private static Logger logger = Logger.getInstance();
    private static DataProcessor dataProcessor;
    private static UserInterface ui;
    private static Scanner scanner;

    // Declare static data structures
    private static List<PropertyData> propertyData = new ArrayList<>();
    private static List<PopulationData> populationData = new ArrayList<>();
    private static List<CovidData> covidData = new ArrayList<>();
    
    public static void main(String[] args) throws IOException {
        // Process command-line arguments
        processArguments(args);

        // Log initial program execution
        logEvent(String.join(" ", args));

        // Initialize components
        dataProcessor = new DataProcessor(covidData, propertyData, populationData);
        ui = new UserInterface();
        scanner = new Scanner(System.in);

        // Main program loop
        while (true) {
            ui.displayMenu();
            int option = scanner.nextInt();
            logEvent(String.valueOf(option));

            if (!processMenuOption(option)) {
                break;
            }
        }

        scanner.close();
    }

    private static void processArguments(String[] args) throws IOException {
        String filePattern = "^--(?<name>.+?)=(?<value>.+)$";
        Pattern pattern = Pattern.compile(filePattern);

        try {
            for (String arg : args) {
                Matcher matcher = pattern.matcher(arg);
                if (matcher.matches()) {
                    String name = matcher.group("name");
                    String value = matcher.group("value");
                    switch (name) {
                        case "population":
                            PopulationDataReader populationReader = new PopulationDataReader(value);
                            populationData = populationReader.readPopulationData();
                            break;
                        case "properties":
                            PropertyDataReader propertyReader = new PropertyDataReader(value);
                            propertyData = propertyReader.readPropertyData();
                            break;
                        case "covid":
                            CovidDataReader covidReader = value.endsWith(".csv") ? new CovidCSVReader(value) : new CovidJSONReader(value);
                            covidData = covidReader.readCovidData();
                            break;
                        case "log":
                            logger.setDestination(value);
                            break;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static boolean processMenuOption(int option) {
        switch (option) {
            case 0:
                System.out.println("Exiting the program");
                return false;
            case 2:
                logEvent(populationFilename);
                int totalPopulation = dataProcessor.getTotalPopulation();
                System.out.println(formatOutput(String.valueOf(totalPopulation)));
                System.out.println("Total Population: " + totalPopulation + "\n");
                break;
            case 3:
                String typeOfVaccination;
                do {
                    System.out.println("Do you want 'partial' or 'full' vaccinations?");
                    typeOfVaccination = scanner.next().toLowerCase();
                    logEvent(typeOfVaccination);
                    if (!typeOfVaccination.equals("partial") && !typeOfVaccination.equals("full")) {
                        System.out.println("That's not a valid option, try again.");
                    }
                } while (!typeOfVaccination.equals("partial") && !typeOfVaccination.equals("full"));

                String date;
                Pattern datePattern = Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$");
                do {
                    System.out.println("Can you please enter a date in the format (YYYY-MM-DD): ");
                    date = scanner.next();
                    logEvent(date);
                    if (!datePattern.matcher(date).matches()) {
                        System.out.println("That's not a valid date format, try again.");
                    }
                } while (!datePattern.matcher(date).matches());

                logEvent(covidFilename + " " + populationFilename);
                // Replace with actual implementation
                boolean isPartial = typeOfVaccination.equals("partial");
                Map<String, Double> vaccinationsPerCapita = dataProcessor.getVaccinationsPerCapita(date, isPartial);
                
                if (vaccinationsPerCapita.isEmpty()) {
                    System.out.println(formatOutput("0"));
                } else {
                    StringBuilder output = new StringBuilder();
                    vaccinationsPerCapita.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .forEach(entry -> {
                                output.append(entry.getKey()).append(" ")
                                      .append(String.format("%.4f", entry.getValue())).append("\n");
                            });
                    System.out.println(formatOutput(output.toString().trim()));
                }
                break;
            case 4:
            case 5:
            case 6:
            case 7:
                String zipCode;
                do {
                    System.out.println("Can you please enter a 5 digit zip code: ");
                    zipCode = scanner.next();
                    logEvent(zipCode);
                    if (!zipCode.matches("\\d{5}")) {
                        System.out.println("That's not a valid zip code, try again.");
                    }
                } while (!zipCode.matches("\\d{5}"));

                switch (option) {
                    case 4:
                        logEvent(propertyFilename + " " + populationFilename);
                        double averageMarketValue = dataProcessor.getAverageMarketValue(zipCode);
                        System.out.println(formatOutput(String.valueOf(averageMarketValue)));
                        System.out.println("Average Market Value for " + zipCode + ": " + averageMarketValue + "\n");
                        break;
                    case 5:
                        logEvent(propertyFilename);
                        double averageLivableArea = dataProcessor.getAverageLivableArea(zipCode);
                        System.out.println(formatOutput(String.valueOf(averageLivableArea)));
                        System.out.println("Average Livable Area for " + zipCode + ": " + averageLivableArea + "\n");
                        break;
                    case 6:
                        logEvent(propertyFilename + " " + populationFilename);
                        int totalMarketValuePerCapita = dataProcessor.getTotalMarketValuePerCapita(zipCode);
                        System.out.println(formatOutput(String.valueOf(totalMarketValuePerCapita)));
                        System.out.println("Total Market Value Per Capita for " + zipCode + ": " + totalMarketValuePerCapita);
                        break;
                    case 7:
                        logEvent(propertyFilename + " " + populationFilename + " " + covidFilename);
                        // Replace with actual implementation
                        System.out.println(formatOutput("Get Correlation"));
                        System.out.println("Get Correlation"); // Replace with actual implementation aswell
                        break;
                }
                break;
            default:
                System.out.println("That's not a valid option, try again: \n");
                logEvent("Invalid option: " + option);
        }
        return true;
    }
    private static void logEvent(String event) {
        String currentTime = String.valueOf(System.currentTimeMillis());
        logger.logEvent(currentTime + " " + event);
    }
    
    private static String formatOutput(String result) {
        return "BEGIN OUTPUT\n" + result + "\nEND OUTPUT";
    }
}