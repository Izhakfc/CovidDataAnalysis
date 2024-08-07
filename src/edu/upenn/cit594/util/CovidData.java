package edu.upenn.cit594.util;

public class CovidData {

		String zipCode;
		int partiallyVaccinated;
		int fullyVaccinated;
		String dateTime;
		

		public CovidData(String zipCode, int partiallyVaccinated, int fullyVaccinated, String dateTime) {
			this.zipCode = zipCode;
			this.partiallyVaccinated = partiallyVaccinated;
			this.fullyVaccinated = fullyVaccinated;
			this.dateTime = dateTime;
		}
		
		public String toString() {
			return "zipCode " + zipCode + " " + "partiallyVaccinated " + partiallyVaccinated + " " + "fullyVaccinated " + fullyVaccinated + " " + "dateTime " + dateTime;
			
		}

		public String getDateTime() {
			// TODO Auto-generated method stub
			return this.dateTime;
		}
		public String getZipCode() {
			// TODO Auto-generated method stub
			return this.zipCode;
		}

		public Integer getPartialVaccinations() {
			// TODO Auto-generated method stub
			return this.partiallyVaccinated;
		}

		public Integer getFullVaccinations() {
			// TODO Auto-generated method stub
			return this.fullyVaccinated;
		}


}