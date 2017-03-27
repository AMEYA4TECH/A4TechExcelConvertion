package parser.psl;

import java.util.ArrayList;
import java.util.List;







import java.util.stream.Collectors;

import com.a4tech.lookup.service.LookupServiceData;
import com.a4tech.lookup.service.restService.LookupRestService;
import com.a4tech.product.model.BatteryInformation;
import com.a4tech.product.model.Combo;
import com.a4tech.product.model.ImprintMethod;
import com.a4tech.product.model.Material;
import com.a4tech.product.model.Packaging;
import com.a4tech.util.CommonUtility;

public class PSLProductAttributeParser {
	
	private LookupServiceData lookupServiceDataObj;
	private LookupRestService lookupRestServiceObj;
	
	public List<BatteryInformation> getBatteyInfo(String BatteryInfo) {
		
		List<BatteryInformation> batteryInfoList = new ArrayList<BatteryInformation>();
		BatteryInformation batinfoObj=new BatteryInformation();
		
		batinfoObj.setName(BatteryInfo);
		batteryInfoList.add(batinfoObj);
		
		return batteryInfoList;
	}

	public List<Packaging> getPackageInfo(String packageInfo) {
		List<Packaging> listOfPackaging = new ArrayList<Packaging>();
		Packaging packageObj= new Packaging();
		packageObj.setName(packageInfo);
		listOfPackaging.add(packageObj);
		
		return listOfPackaging;
	}

	public List<ImprintMethod> getImprintMethodValue(String imprintMethod) {
			
		List<ImprintMethod> imprintMethodsList = new ArrayList<ImprintMethod>();
		imprintMethod=imprintMethod.replace("engraved", "");
		ImprintMethod imprMethod = new ImprintMethod();
	
		List<String> finalImprintValues = getImprintValue(imprintMethod.toUpperCase().trim());	
		String imprintMethodArr[]=imprintMethod.split(",");
		for (String Value : imprintMethodArr){
		  if(Value.contains("logo") || Value.contains("print")){
			  ImprintMethod imprMethod1= new ImprintMethod();
			  imprMethod1.setAlias(Value);
			  imprMethod1.setType("OTHER");
			  imprintMethodsList.add(imprMethod1);
			  }
		    else if(Value.contains("Laser"))
		    {
		    	ImprintMethod imprMethod2= new ImprintMethod();
		    	imprMethod2.setAlias("LASER ENGRAVED");
		    	imprMethod2.setType("LASER ENGRAVED");
				  imprintMethodsList.add(imprMethod2);
		    }
		}
	    	for (String innerValue : finalImprintValues) {
		    	 imprMethod = new ImprintMethod();
				 imprMethod.setAlias(innerValue);
				 imprMethod.setType(innerValue);
				 imprintMethodsList.add(imprMethod);  
		       }
	    	
		return imprintMethodsList;
	}
	

	public List<String> getCompliance(String certificateValue) {
		List<String> complianceList = new ArrayList<String>();
		
		String complainceArr[]=certificateValue.split(",");
		
		for (String Value : complainceArr) {
			complianceList.add(Value);
		}
		return complianceList;
	}
	
	
	public List<Material> getMaterialList(String materialValue) {
		String MaterialCombo =materialValue;
		List<Material> listOfMaterial = new ArrayList<>();
		Material materialObj = new Material();
		MaterialCombo=MaterialCombo.replace("+", ",").replace("Cloth","Other Fabric").replace("/", ",");
		if(MaterialCombo.contains(",")){
		String MaterialArr[]=MaterialCombo.split(",");
		MaterialCombo="";
		MaterialCombo=MaterialCombo.concat(MaterialArr[0]).concat(",").concat(MaterialArr[1]);
		}
		if(MaterialCombo.contains("Abs plastic") || MaterialCombo.contains("Abs plastics"))
		{
			List<String> listOfLookupMaterial = getMaterialType(MaterialCombo
					.toUpperCase());
			materialObj = new Material();
			String Combo1 = "Other";
			String Combo2 = listOfLookupMaterial.get(2);
			Combo comboObj = new Combo();
			materialObj.setName(Combo1);
			materialObj.setAlias(materialValue);
			comboObj.setName(Combo2);
			materialObj.setCombo(comboObj);
			listOfMaterial.add(materialObj);
		}
		else{
			if(MaterialCombo.contains("Abs") || MaterialCombo.contains("ABS"))
			{
			 MaterialCombo=MaterialCombo.replace("Abs", "Abs  ").replace("ABS", "ABS  ");
			}
		List<String> listOfLookupMaterial = getMaterialType(MaterialCombo
					.toUpperCase());
	    int numOfMaterials = listOfLookupMaterial.size();
		if (!listOfLookupMaterial.isEmpty()) {
			if (numOfMaterials == 1) {
				materialObj = new Material();
				materialObj = getMaterialValue(
						listOfLookupMaterial.toString(), MaterialCombo);
				listOfMaterial.add(materialObj);
			}else
			{
				materialObj = new Material();
				String Combo1 = listOfLookupMaterial.get(0);
				String Combo2 = listOfLookupMaterial.get(1);
				Combo comboObj = new Combo();
				materialObj.setName(Combo1);
				materialObj.setAlias(materialValue);
				comboObj.setName(Combo2);
				if(materialValue.contains(" Cloth"))
				{
			     comboObj.setName(listOfLookupMaterial.get(2));
				}
				materialObj.setCombo(comboObj);
				listOfMaterial.add(materialObj);
			}
		}else
		{
			materialObj = getMaterialValue("Other", materialValue);
			listOfMaterial.add(materialObj);
		}
		}
		return listOfMaterial;
	}
	
	
	
	public List<String> getMaterialType(String value) {
		List<String> listOfLookupMaterials = lookupServiceDataObj
				.getMaterialValues();
		List<String> finalMaterialValues = listOfLookupMaterials.stream()
				.filter(mtrlName -> value.contains(mtrlName))
				.collect(Collectors.toList());

		return finalMaterialValues;
	}
	
	public Material getMaterialValue(String name, String alias) {
		Material materialObj = new Material();
		name = CommonUtility.removeCurlyBraces(name);
		materialObj.setName(name);
		materialObj.setAlias(alias);
		return materialObj;
	}
		
	public List<String> getImprintValue(String value){
		List<String> imprintLookUpValue = lookupServiceDataObj.getImprintMethods();
		List<String> finalImprintValues = imprintLookUpValue.stream()
				                                  .filter(impntName -> value.contains(impntName))
				                                  .collect(Collectors.toList());
                                                 
				
		return finalImprintValues;	
	}

	public LookupServiceData getLookupServiceDataObj() {
		return lookupServiceDataObj;
	}

	public void setLookupServiceDataObj(LookupServiceData lookupServiceDataObj) {
		this.lookupServiceDataObj = lookupServiceDataObj;
	}

	public LookupRestService getLookupRestServiceObj() {
		return lookupRestServiceObj;
	}

	public void setLookupRestServiceObj(LookupRestService lookupRestServiceObj) {
		this.lookupRestServiceObj = lookupRestServiceObj;
	}
	
	
	
}
