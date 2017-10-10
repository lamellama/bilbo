package bilbo.arunwebnerd.com.bilbo;
import java.util.*;
import org.apache.http.conn.ssl.*;

import android.os.Bundle;
import android.util.*;
import android.content.Context;

public class ItemCalculator
{
	private final static String TAG = "ItemCalculator";
	private ArrayList<PerPersonValue> ppValues;
	private HashMap<Integer, List<Integer>> groupIndexMap;
	private float totalExtraValue =0;
	private int numPeople;
	private float billTotal;
	private int tipPercent;
	private int uniqueIndex = -1;

	public void saveInstance(Bundle storageBundle){

		storageBundle.putParcelableArrayList("peeps", ppValues);
		storageBundle.putSerializable("map", groupIndexMap);
	}
	
	public void restoreState(Bundle storageBundle){
		groupIndexMap = (HashMap<Integer, List<Integer>>)storageBundle.getSerializable("map");
	}
	
	public boolean isThisAGroup(int g){
		if(g < 0)
			return true;
		return false;
	}
	
	public ItemCalculator(int numPeeps, float bill, int tip){
		numPeople = numPeeps;
		billTotal = bill;
		tipPercent = tip;
		ppValues = new ArrayList<PerPersonValue>();
		initPPValueList();
		calculatePerPersonValues();
		groupIndexMap = new HashMap <Integer, List<Integer>>();
	}
	
	public ItemCalculator(int numPeeps, float bill, int tip, ArrayList<PerPersonValue> ppList){
		numPeople = numPeeps;
		billTotal = bill;
		tipPercent = tip;
		ppValues = ppList;
		groupIndexMap = new HashMap <Integer, List<Integer>>();
	}

	private void initPPValueList(){
		for(int i = 0; i < numPeople; i++)
			ppValues.add(new PerPersonValue(0, 0, 0));
	}
	
	private void addToGroup(List<Integer> items, int group){
		List<Integer> groupList = groupIndexMap.get(group);
		
		for(Integer i = 0; i < items.size(); i++){
			
			if(!groupList.contains(items.get(i))){
				groupList.add(items.get(i));
				ppValues.get(items.get(i)).group = group;
				Log.d(TAG, "addToGroup()" + group + ": " + items.get(i));
				}
			
		}
		for(int i = 0; i < groupList.size(); i++){
			Log.d(TAG, "groupList after: " + groupIndexMap.get(group).get(i));
		}
			
	}
	
	//Takes a list of group index
	//Merges multiple groups into one and 
	//returns new group index
	public int mergeGroups(List<Integer> groups){
		if(groups.size() > 1)
			if(groupIndexMap.containsKey(groups.get(0)))
			for(int i = 1; i < groups.size(); i ++){
				//Merge two lists
				groupIndexMap.get(groups.get(0)).addAll(groupIndexMap.get(groups.get(i)));
				
				//Switch their group value
				for(int j = 0; j < groupIndexMap.get(groups.get(i)).size(); j++)
					ppValues.get(groupIndexMap.get(groups.get(i)).get(j)).group = groups.get(0);
				
				//delete other group lists
				groupIndexMap.remove(groups.get(i));
				groups.remove(i);
			}
		return groups.get(0);
	}
	
	public void breakGroup(int groupIndex){
		Log.d(TAG, "remove group: " + groupIndex);
		if(groupIndexMap.containsKey(groupIndex)){
			Log.d(TAG, "destroy group ");
			for(int i = 0; i < groupIndexMap.get(groupIndex).size(); i ++){
				ppValues.get(groupIndexMap.get(groupIndex).get(i)).group = 0;
				}
				
			groupIndexMap.remove(groupIndex);
		
		}
	}
	
	//This is where the meat is
	public void makeGroup(List<Integer> items){
		List<Integer> groups = new ArrayList<Integer>();
		
		//Seperate groups and individuals lists
		for(int z = 0; z < items.size();){
			if(items.get(z) < 0){//this is a group
				//item is a group
				groups.add(items.get(z));
				items.remove(z);
			}else{z++;}
		}
		
		if(groups.size() > 1){ //Multiple groups
			//Merge groups
			mergeGroups(groups);
			
		}
		
		//Check there is anything else to merge
		if(items.size() < 1)
			return;

		//One group in list, add all individuals to this group
		if((groups.size() == 1)&&(items.size() > 0)){
			addToGroup(items, groups.get(0));
			return;
		}
			
		//items not already in groups, so make a new one
		int group = Integer.valueOf(uniqueIndex);
		uniqueIndex--;
		//Add list to map
		groupIndexMap.put(group, items);
		//Set persons group value
		Log.d(TAG, "make new group: " + group);
		for(int i = 0; i < items.size(); i ++)
			if((items.get(i) < ppValues.size())&&(items.get(i) >=0)){
				ppValues.get(items.get(i)).group = group;
				Log.d(TAG, "item " + items.get(i) + " group set to: " + group);
				}
			
	}
	
	private void calculatePerPersonValues(){
		float totalLeft = 0;
		float perPerson = 0;
		if(billTotal > totalExtraValue)
			totalLeft = billTotal - totalExtraValue;
		if((totalLeft > 0)&&(ppValues.size() > 0))
			perPerson =  totalLeft / ppValues.size();
		for(int i =0; i<ppValues.size(); i++){
			ppValues.get(i).bill = ppValues.get(i).addedExtra + perPerson;
			ppValues.get(i).tipPercent = tipPercent;
		}
		
	}
	
	public void addExtraValue(int index, float val){
		if((index >= 0)&&(index < ppValues.size())){
			ppValues.get(index).addedExtra+=val;
			totalExtraValue+=val;
			calculatePerPersonValues();
		}
	}
	
	public void setItemText(int index, String text){
		if(index >= 0)//If it is < 0 it is a group
			ppValues.get(index).name = text;
	}
	
	
	
	public List<PerPersonValue> getPPValueList(){
	
		int i;
		List<PerPersonValue> dataSet = new ArrayList<PerPersonValue>();
		
		//Combine groups imto single PP
		for (Map.Entry<Integer, List <Integer>> entry : groupIndexMap.entrySet()) {
			Integer groupKey = entry.getKey();
			List<Integer> groupList = entry.getValue();
			
			PerPersonValue grouped = new PerPersonValue(0, 0, 0);
			grouped.group = groupKey;
			grouped.realIndex = groupKey;
			
			//create name for group, this is not saved
			grouped.name = "Group ";
			if(groupList.size() > 0)
				grouped.name += ppValues.get(groupList.get(0)).name;
			
			float tip = 0;
			int k = 0;
			for(; k< groupList.size(); k++){
				grouped.addedExtra += ppValues.get(groupList.get(k)).addedExtra;
				grouped.bill += ppValues.get(groupList.get(k)).bill;
				tip += ppValues.get(groupList.get(k)).tipPercent;
				//TODO grouped. += ppValues[tempList.get(k)].addedExtra;

			}
			if((k > 0)&&(tip > 0))
				grouped.tipPercent =(int) tip / k;
			Log.d(TAG, "Add group to dataset");
			dataSet.add(grouped);

		}
		
		//Add individuals and also record thier positions in ppValues
		for(i = 0; i < ppValues.size(); i++){
			if(ppValues.get(i).group == 0){
				ppValues.get(i).realIndex = i;
				dataSet.add(ppValues.get(i));
				}
		}
		return dataSet;
	}
}
