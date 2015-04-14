package lu.itrust.business.TS.model.riskinformation.helper;

import java.util.Comparator;

import lu.itrust.business.TS.model.riskinformation.RiskInformation;

public class RiskInformationComparator implements Comparator<RiskInformation> {

	@Override
	public int compare(RiskInformation o1, RiskInformation o2) {
		if(o1.getCategory().equals(o2.getCategory()))
			return compare(o1.getChapter(), o2.getChapter());
		else return comprareCategory(o2.getCategory(), o1.getCategory());
	}
	
	private int comprareCategory(String category1, String category2){
		if(category1.equalsIgnoreCase("Threat"))
			return 1;
		else if(category2.equalsIgnoreCase("Threat"))
			return -1;
		else if(category1.equalsIgnoreCase("Vul"))
			return 1;
		else if(category2.equalsIgnoreCase("Vul"))
			return -1;
		else if(category1.equalsIgnoreCase("Risk_TBS"))
			return 1;
		else return -1;
	}
	
	private int compare(String reference1, String reference2){
		String [] values1 = reference1.split("\\.", 2);
		String [] values2 = reference2.split("\\.", 2);
		int value1 = toInt(values1[0]);
		int value2 = toInt(values2[0]);
		if(value1==value2){
			if(values1.length == 1 && values2.length==1)
				return 0;
			else if(values1.length==1 && values2.length>1)
				return -1;
			else if(values1.length>1 && values2.length==1)
				return 1;
			else return compare(values1[1], values2[1]);
		}
		else 
			return Integer.compare(value1, value2);
	}
	
	private int toInt(String value){
		try{
			return Integer.parseInt(value);
		}catch(NumberFormatException e){
			return 0;
		}
	}

}
